package com.ssrs.elasticsearch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.elasticsearch.code.FieldWeightStatus;
import com.ssrs.elasticsearch.mapper.FieldWeightMapper;
import com.ssrs.elasticsearch.model.entity.FieldWeight;
import com.ssrs.elasticsearch.model.form.FieldWeightPageForm;
import com.ssrs.elasticsearch.model.vo.FieldWeightVo;
import com.ssrs.elasticsearch.service.IFieldWeightService;
import com.ssrs.elasticsearch.util.ClassUtil;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-08-23
 */
@Service
public class FieldWeightServiceImpl extends ServiceImpl<FieldWeightMapper, FieldWeight> implements IFieldWeightService {
    private static final String PACKAGE_NAME = "com.zving.essearch.entity";
    private static final Class<com.ssrs.elasticsearch.annotation.FieldWeight> annotationClass = com.ssrs.elasticsearch.annotation.FieldWeight.class;

    /**
     * 初始化FieldWeight表
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void initFWTable() {
        List<FieldWeight> fieldWeights = list();
        List<String> fieldList = ClassUtil.getFieldListByAnnotation(PACKAGE_NAME, annotationClass);
        ArrayList<String> existingFields = new ArrayList<>();
        // 对比库中和注解中的field
        for (String field : fieldList) {
            for (int i = 0; i < fieldWeights.size(); i++) {
                if (field.equals(fieldWeights.get(i).getField())) {
                    existingFields.add(field);
                }
            }
        }
        fieldList.removeAll(existingFields);
        // 将所有状态设为enabled,使之前删过的注解可以重新显示
        update(new LambdaUpdateWrapper<FieldWeight>().set(FieldWeight::getStatus, FieldWeightStatus.ENABLED));
        // 对注解中对比后剩下的添加到库中
        for (String field : fieldList) {
            FieldWeight fieldWeight = new FieldWeight();
            fieldWeight.setField(field);
            fieldWeight.setWeight(1);
            fieldWeight.setStatus(FieldWeightStatus.ENABLED);
            save(fieldWeight);
        }
    }

    @Override
    public Page pageList(FieldWeightPageForm fieldWeightPageForm) {
        this.initFWTable();
        IPage<FieldWeight> iPage = page(new Query<FieldWeight>().getPage(fieldWeightPageForm), new LambdaQueryWrapper<FieldWeight>()
                .like(StrUtil.isNotEmpty(fieldWeightPageForm.getField()), FieldWeight::getField, fieldWeightPageForm.getField())
                .orderByDesc(FieldWeight::getId));
        return new Page(iPage);
    }

    @Override
    public FieldWeightVo init(long id) {
        FieldWeight fieldWeight = getById(id);
        return BeanUtil.toBean(fieldWeight, FieldWeightVo.class);
    }

    @Override
    public void saveWeight(long id, Map<String, Object> params) {
        Integer weight = Convert.toInt(params.get("weight"));
        FieldWeight fieldWeight = new FieldWeight();
        fieldWeight.setId(id);
        fieldWeight.setWeight(weight);
        updateById(fieldWeight);
    }
}
