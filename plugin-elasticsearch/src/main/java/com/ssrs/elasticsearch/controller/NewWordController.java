package com.ssrs.elasticsearch.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ssrs.elasticsearch.code.WordStatus;
import com.ssrs.elasticsearch.model.entity.Dictionary;
import com.ssrs.elasticsearch.model.form.NewWordAddForm;
import com.ssrs.elasticsearch.model.form.NewWordEditForm;
import com.ssrs.elasticsearch.model.form.NewWordPageForm;
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
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ssrs
 * @since 2020-08-29
 */
@RestController
@RequestMapping("/api/new-word")
public class NewWordController extends BaseController {
    @Autowired
    private IDictionaryService dictionaryService;

    @Priv
    @GetMapping("/{id}")
    public ApiResponses<Dictionary> get(@PathVariable("id") long id) {
        return success(dictionaryService.info(id));
    }

    @Priv
    @GetMapping
    public ApiResponses<Page> list(@Validated NewWordPageForm newWordPageForm) {
        IPage<Dictionary> iPage = dictionaryService.page(new Query<Dictionary>().getPage(newWordPageForm), new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getType, WordStatus.TYPE_NEW)
                .like(StrUtil.isNotEmpty(newWordPageForm.getWord()), Dictionary::getRootWord, newWordPageForm.getWord())
                .orderByDesc(Dictionary::getCreateTime));
        return success(new Page(iPage));
    }

    @Priv(SearchWordManagerPriv.NEW_WORD_ADD)
    @PostMapping
    public ApiResponses<String> add(@Validated NewWordAddForm newWordAddForm) {
        dictionaryService.saveNewWord(newWordAddForm);
        return success("保存成功");
    }

    @Priv(SearchWordManagerPriv.NEW_WORD_EDIT)
    @PutMapping("/{id}")
    public ApiResponses<String> update(@PathVariable("id") long id, @Validated NewWordEditForm newWordEditForm) {
        dictionaryService.editNewWord(id, newWordEditForm);
        return success("修改成功");
    }

    @Priv(SearchWordManagerPriv.STOP_WORD_DEL)
    @DeleteMapping("/{ids}")
    public ApiResponses<String> delete(@PathVariable("ids") String ids) {
        dictionaryService.deleteNewWord(ids);
        return success("删除成功");
    }
}
