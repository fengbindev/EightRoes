package com.ssrs.platform.model;

import lombok.Data;

@Data
public class Menu {
    private String id;
    private String parentId;
    private String name;
    private String memo;
    private Menu children;
}
