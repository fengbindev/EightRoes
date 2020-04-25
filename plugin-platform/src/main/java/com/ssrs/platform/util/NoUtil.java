package com.ssrs.platform.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.platform.model.entity.Maxno;
import com.ssrs.platform.service.IMaxnoService;
import org.springframework.stereotype.Component;

@Component
public class NoUtil {
    private static Log log = LogFactory.get();
    private static IMaxnoService maxnoService;

    public NoUtil(IMaxnoService maxnoService) {
        NoUtil.maxnoService = maxnoService;
    }

    /**
     * @param noType  类型
     * @param subType 子类型
     * @param size    一次申请的ID数
     * @return
     */
    private static long getMaxID(String noType, String subType, int size) {
        if (size < 1) {
            size = 1;
        }
        Maxno maxno = maxnoService.getOne(Wrappers.<Maxno>lambdaQuery().eq(Maxno::getNoType, noType).eq(Maxno::getNoSubType, subType));
        if (ObjectUtil.isNull(maxno)) {
            maxno = new Maxno()
                    .setNoType(noType)
                    .setNoSubType(subType)
                    .setNoMaxValue(Convert.toLong(size));
            if (maxnoService.save(maxno)) {
                return size;
            } else {
                throw new RuntimeException("获取最大号时发生错误!");
            }
        } else {
            long t = maxno.getNoMaxValue() + size;
            boolean update = maxnoService.update(Wrappers.<Maxno>lambdaUpdate().setSql("no_maxValue=" + t).eq(Maxno::getNoType, noType).eq(Maxno::getNoSubType, subType));
            if (update) {
                return t;
            } else {
                throw new RuntimeException("获取最大号时发生错误!");
            }

        }
    }

    /**
     * 得到类型为noType位长为length的编码
     */
    public static String getMaxNo(String noType, int length) {
        long t = getMaxID(noType, "SN", 1);
        String no = String.valueOf(t);
        if (no.length() > length) {
            return no.substring(0, length);
        }
        return StrUtil.padPre(no, length, '0');
    }

    /**
     * 得到类型为noType，位长为length且前缀为prefix的编码
     */
    public static String getMaxNo(String noType, String prefix, int length) {
        long t = getMaxID(noType, prefix, 1);
        String no = String.valueOf(t);
        if (no.length() > length) {
            log.warn("获取最大编号时发现长度超出预期：NoType=" + noType + ",Length=" + length + ",MaxValue=" + t);
            return no.substring(no.length() - length);
        }
        return prefix + StrUtil.padPre(no, length, '0');
    }
}
