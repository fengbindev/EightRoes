package com.ssrs.platform;

import com.ssrs.framework.extend.IExtendItem;

import java.util.ArrayList;
import java.util.List;

public class FixedCodeType implements IExtendItem {
    boolean allowAddItem = true;
    boolean multiLevel = false;
    String codeType;
    String codeName;
    List<FixedCodeItem> fixedItems = new ArrayList<FixedCodeItem>();

    public FixedCodeType(String codeType, String codeName, boolean allowAddItem, boolean multiLevel) {
        this.codeType = codeType;
        this.codeName = codeName;
        this.allowAddItem = allowAddItem;
        this.multiLevel = multiLevel;
    }

    public boolean contains(String codeValue) {
        for (FixedCodeItem item : fixedItems) {
            if (item.getValue().equals(codeValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getExtendItemID() {
        return getCodeType();
    }

    public String getCodeType() {
        return codeType;
    }

    @Override
    public String getExtendItemName() {
        return getCodeName();
    }

    public List<FixedCodeItem> getFixedItems() {
        return fixedItems;
    }


    public void addFixedItem(String itemValue, String itemName, String memo) {
        fixedItems.add(new FixedCodeItem(itemValue, itemName, memo));
    }

    public boolean allowAddItem() {
        return allowAddItem;
    }

    public boolean isMultiLevel() {
        return multiLevel;
    }

    public void setCodeName(String codeName) {

        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }

    public static class FixedCodeItem {
        private String value;// CodeValue
        private String name;// CodeName
        private String memo;

        public FixedCodeItem(String value, String name, String memo) {
            this.value = value;
            this.name = name;
            this.memo = memo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

    }
}
