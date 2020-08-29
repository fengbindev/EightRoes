package com.ssrs.elasticsearch.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ssrs.elasticsearch.code.WordStatus;
import com.ssrs.elasticsearch.model.entity.Dictionary;
import com.ssrs.elasticsearch.model.form.StopWordAddForm;
import com.ssrs.elasticsearch.model.form.StopWordEditForm;
import com.ssrs.elasticsearch.model.form.StopWordPageForm;
import com.ssrs.elasticsearch.priv.SearchWordManagerPriv;
import com.ssrs.elasticsearch.service.IDictionaryService;
import com.ssrs.framework.security.annotation.Priv;
import com.ssrs.framework.web.ApiResponses;
import com.ssrs.framework.web.BaseController;
import com.ssrs.platform.util.Page;
import com.ssrs.platform.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author ssrs
 */
@RestController
@RequestMapping("/api/stop-word")
public class StopWordController extends BaseController {
    @Autowired
    private IDictionaryService dictionaryService;

    @Priv
    @GetMapping("/{id}")
    public ApiResponses<Dictionary> get(@PathVariable("id") long id) {
        return success(dictionaryService.info(id));
    }

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@Validated StopWordPageForm stopWordPageForm) {
        IPage<Dictionary> iPage = dictionaryService.page(new Query<Dictionary>().getPage(stopWordPageForm), new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getType, WordStatus.TYPE_STOP)
                .like(StrUtil.isNotEmpty(stopWordPageForm.getWord()), Dictionary::getRootWord, stopWordPageForm.getWord())
                .orderByDesc(Dictionary::getCreateTime));
        return success(new Page(iPage));
    }

    @Priv(SearchWordManagerPriv.STOP_WORD_ADD)
    @PostMapping
    public ApiResponses<String> add(@Validated StopWordAddForm stopWordAddForm) {
        dictionaryService.saveStopWord(stopWordAddForm);
        return success("保存成功");
    }

    @Priv(SearchWordManagerPriv.STOP_WORD_EDIT)
    @PutMapping("/{id}")
    public ApiResponses<String> update(@PathVariable("id") long id, @Validated StopWordEditForm stopWordEditForm) {
        dictionaryService.editStopWord(id, stopWordEditForm);
        return success("修改成功");
    }

    @Priv(SearchWordManagerPriv.STOP_WORD_DEL)
    @DeleteMapping("/{ids}")
    public ApiResponses<String> delete(@PathVariable("ids") String ids) {
        dictionaryService.deleteStopWord(ids);
        return success("删除成功");
    }
}
