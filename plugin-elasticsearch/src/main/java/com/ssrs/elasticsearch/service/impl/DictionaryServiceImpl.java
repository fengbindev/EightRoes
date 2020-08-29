package com.ssrs.elasticsearch.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssrs.elasticsearch.code.WordStatus;
import com.ssrs.elasticsearch.mapper.DictionaryMapper;
import com.ssrs.elasticsearch.model.entity.Dictionary;
import com.ssrs.elasticsearch.model.form.NewWordAddForm;
import com.ssrs.elasticsearch.model.form.NewWordEditForm;
import com.ssrs.elasticsearch.model.form.StopWordAddForm;
import com.ssrs.elasticsearch.model.form.StopWordEditForm;
import com.ssrs.elasticsearch.service.IDictionaryService;
import com.ssrs.framework.web.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-08-29
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    @Override
    public Dictionary info(long id) {
        return getById(id);
    }

    @Override
    public void saveNewWord(NewWordAddForm newWordAddForm) {
        int count = count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getRootWord, newWordAddForm.getWord()));
        if (count > 0) {
            throw new ApiException("已存在的新词！");
        }
        Dictionary dictionary = new Dictionary();
        dictionary.setRootWord(newWordAddForm.getWord());
        dictionary.setType(WordStatus.TYPE_NEW);
        save(dictionary);
    }

    @Override
    public void editNewWord(long id, NewWordEditForm newWordEditForm) {
        Dictionary dictionary = getById(id);
        int count = count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getRootWord, newWordEditForm.getWord()));
        if (!dictionary.getRootWord().equals(newWordEditForm.getWord()) && count > 0) {
            throw new ApiException("已存在的新词！");
        }
        dictionary.setRootWord(newWordEditForm.getWord());
        dictionary.setType(WordStatus.TYPE_NEW);
        updateById(dictionary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNewWord(String ids) {
        List<String> delIds = StrUtil.split(ids, ',');
        List<Dictionary> dictionaries = listByIds(delIds);
        if (CollUtil.isEmpty(dictionaries)) {
            throw new ApiException("待删除的记录不存在");
        }
        removeByIds(dictionaries);
    }

    @Override
    public void saveStopWord(StopWordAddForm stopWordAddForm) {
        int count = count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getRootWord, stopWordAddForm.getWord()));
        if (count > 0) {
            throw new ApiException("已存在的停用词！");
        }
        Dictionary dictionary = new Dictionary();
        dictionary.setRootWord(stopWordAddForm.getWord());
        dictionary.setType(WordStatus.TYPE_STOP);
        save(dictionary);
    }

    @Override
    public void editStopWord(long id, StopWordEditForm stopWordEditForm) {
        Dictionary dictionary = getById(id);
        int count = count(new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getRootWord, stopWordEditForm.getWord()));
        if (!dictionary.getRootWord().equals(stopWordEditForm.getWord()) && count > 0) {
            throw new ApiException("已存在的停用词！");
        }
        dictionary.setRootWord(stopWordEditForm.getWord());
        dictionary.setType(WordStatus.TYPE_STOP);
        updateById(dictionary);
    }

    @Override
    public void deleteStopWord(String ids) {
        List<String> delIds = StrUtil.split(ids, ',');
        List<Dictionary> dictionaries = listByIds(delIds);
        if (CollUtil.isEmpty(dictionaries)) {
            throw new ApiException("待删除的记录不存在");
        }
        removeByIds(dictionaries);
    }
}
