package com.ssrs.platform.service;

import com.ssrs.platform.model.entity.Privilege;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author ssrs
 * @since 2020-04-04
 */
public interface IPrivilegeService extends IService<Privilege> {

    void setPriv(ArrayList<String> keys, String typeID, String type);
}
