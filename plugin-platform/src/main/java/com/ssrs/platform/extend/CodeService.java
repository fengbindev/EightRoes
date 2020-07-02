package com.ssrs.platform.extend;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.framework.util.SpringUtil;
import com.ssrs.platform.FixedCodeType;
import com.ssrs.platform.extend.item.CodeCacheProvider;
import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.service.ICodeService;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码项扩展服务
 *
 * @author ssrs
 */
public class CodeService extends AbstractExtendService<FixedCodeType> {
    private static final Log log = LogFactory.get();

    public static CodeService getInstance() {
        return findInstance(CodeService.class);
    }

    /**
     * 将各插件注册的Code持久化到数据库中 代码项如果数据库中以存在则不持久化
     */
    public static void init() {
        ArrayList<String> tmpList = new ArrayList<String>();
        ICodeService codeService = SpringUtil.getBean(ICodeService.class);
        List<Code> codeList = codeService.list(Wrappers.<Code>lambdaQuery().orderByAsc(Code::getCodeOrder, Code::getCodeType, Code::getParentCode));
        for (Code code : codeList) {
            tmpList.add("@CodeType=" + code.getCodeType() + "@ParentCode=" + code.getParentCode() + "@CodeValue=" + code.getCodeValue());
            CodeCacheProvider.setCode(code);
        }
        List<FixedCodeType> codeTypeList = CodeService.getInstance().getAll();
        List<Code> saveCodeList = new ArrayList<>();
        for (FixedCodeType fct : codeTypeList) {
            Code code = new Code();
            code.setCodeType(fct.getCodeType());
            code.setParentCode("System");
            code.setCodeValue("System");
            if (!tmpList.contains("@CodeType=" + fct.getCodeType() + "@ParentCode=System@CodeValue=System")) {
                // 不存在的插入数据库
                code.setCodeName(fct.getCodeName());
                code.setCodeOrder(System.currentTimeMillis());
                saveCodeList.add(code);
                CodeCacheProvider.setCode(code);
            }
            List<FixedCodeType.FixedCodeItem> items = fct.getFixedItems();
            for (FixedCodeType.FixedCodeItem item : items) {
                // 如果数据库不存在则插入
                if (!tmpList.contains(
                        "@CodeType=" + code.getCodeType() + "@ParentCode=" + code.getCodeType() + "@CodeValue=" + item.getValue())) {
                    if (StrUtil.isEmpty(item.getValue())) {
                        continue;
                    }
                    Code codeChild = new Code();
                    codeChild.setCodeType(code.getCodeType());
                    codeChild.setParentCode(code.getCodeType());
                    codeChild.setCodeValue(item.getValue());
                    codeChild.setCodeName(item.getName());
                    codeChild.setCodeOrder(System.currentTimeMillis());
                    codeChild.setMemo(item.getMemo());
                    saveCodeList.add(codeChild);
                    CodeCacheProvider.setCode(codeChild);
                }
            }
        }
        if (ObjectUtil.isNotEmpty(saveCodeList)) {
            boolean b = codeService.saveBatch(saveCodeList);
            if (!b) {
                log.error("代码项code初始化失败！");
            }
        }
    }
}
