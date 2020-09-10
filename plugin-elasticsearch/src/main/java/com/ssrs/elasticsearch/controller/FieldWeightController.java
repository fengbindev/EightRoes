package com.ssrs.elasticsearch.controller;


import com.ssrs.elasticsearch.model.form.FieldWeightPageForm;
import com.ssrs.elasticsearch.model.vo.FieldWeightVo;
import com.ssrs.elasticsearch.priv.FieldWeightManagerPriv;
import com.ssrs.elasticsearch.service.IFieldWeightService;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-08-23
 */
@RestController
@RequestMapping("/api/fieldWeight")
public class FieldWeightController extends BaseController {
    @Autowired
    private IFieldWeightService fieldWeightService;

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@Validated FieldWeightPageForm fieldWeightPageForm) {
        return success(fieldWeightService.pageList(fieldWeightPageForm));
    }

    @Priv
    @GetMapping("/{id}")
    public ApiResponses<FieldWeightVo> init(@PathVariable long id) {
        return success(fieldWeightService.init(id));
    }

    @Priv(FieldWeightManagerPriv.FIELD_WEIGHT_EDIT)
    @PutMapping("/{id}")
    public ApiResponses<String> save(@RequestParam Map<String, Object> params, @PathVariable long id) {
        fieldWeightService.saveWeight(id, params);
        return success("保存成功");
    }

}
