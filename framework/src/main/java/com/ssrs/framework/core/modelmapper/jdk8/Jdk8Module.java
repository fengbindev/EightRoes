package com.ssrs.framework.core.modelmapper.jdk8;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

/**
* @Description:    Supports the JDK8 data types  with  ModelMapper
* @Author:          ssrs
* @CreateDate:     2019/8/24 17:03
* @UpdateUser:     ssrs
* @UpdateDate:     2019/8/24 17:03
* @Version:        1.0
*/
public class Jdk8Module implements Module {

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().getConverters().add(0, new FromOptionalConverter());
        modelMapper.getConfiguration().getConverters().add(0, new ToOptionalConverter());
    }
}
