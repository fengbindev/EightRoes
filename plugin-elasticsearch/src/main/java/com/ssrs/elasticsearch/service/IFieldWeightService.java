package com.ssrs.elasticsearch.service;

import com.ssrs.elasticsearch.model.entity.FieldWeight;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ssrs.elasticsearch.model.form.FieldWeightPageForm;
import com.ssrs.elasticsearch.model.vo.FieldWeightVo;
import com.ssrs.platform.util.Page;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-08-23
 */
public interface IFieldWeightService extends IService<FieldWeight> {

    Page pageList(FieldWeightPageForm fieldWeightPageForm);

    FieldWeightVo init(long id);

    void saveWeight(long id, Map<String, Object> params);
}
