package com.ssrs.elasticsearch.service;

import com.ssrs.elasticsearch.model.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ssrs.elasticsearch.model.form.NewWordAddForm;
import com.ssrs.elasticsearch.model.form.NewWordEditForm;
import com.ssrs.elasticsearch.model.form.StopWordAddForm;
import com.ssrs.elasticsearch.model.form.StopWordEditForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-08-29
 */
public interface IDictionaryService extends IService<Dictionary> {

    Dictionary info(long id);

    void saveNewWord(NewWordAddForm newWordAddForm);

    void editNewWord(long id, NewWordEditForm newWordEditForm);

    void deleteNewWord(String ids);

    void saveStopWord(StopWordAddForm stopWordAddForm);

    void editStopWord(long id, StopWordEditForm stopWordEditForm);

    void deleteStopWord(String ids);
}
