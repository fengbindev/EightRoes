package com.ssrs.platform.service.impl;

import com.ssrs.platform.model.entity.Code;
import com.ssrs.platform.mapper.CodeMapper;
import com.ssrs.platform.service.ICodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ssrs
 * @since 2020-02-25
 */
@Service
public class CodeServiceImpl extends ServiceImpl<CodeMapper, Code> implements ICodeService {

}
