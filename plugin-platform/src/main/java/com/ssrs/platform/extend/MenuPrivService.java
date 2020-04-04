package com.ssrs.platform.extend;

import com.ssrs.framework.extend.AbstractExtendService;
import com.ssrs.platform.priv.AbstractMenuPriv;

public class MenuPrivService extends AbstractExtendService<AbstractMenuPriv> {

    public static MenuPrivService getInstance() {
        return findInstance(MenuPrivService.class);
    }
}
