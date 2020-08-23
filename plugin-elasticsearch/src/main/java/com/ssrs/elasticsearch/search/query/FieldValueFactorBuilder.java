package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class FieldValueFactorBuilder {

    private String fieldName;
    private String modifier;
    private Integer factor;
    private Integer missing;

    public FieldValueFactorBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldValueFactorBuilder setModifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    public FieldValueFactorBuilder setFactor(Integer factor) {
        this.factor = factor;
        return this;
    }

    public JsonElement build() {
        JsonObject fieldValueFactor = object("field", fieldName);
        JsonObject result = object("field_value_factor", fieldValueFactor);
        if(modifier!=null) {
            fieldValueFactor.add("modifier", new JsonPrimitive(modifier));
        }
        if(factor!=null) {
            fieldValueFactor.add("factor", new JsonPrimitive(factor));
        }
        if(missing!=null) {
            fieldValueFactor.add("missing", new JsonPrimitive(missing));
        }
        return result;
    }

    public FieldValueFactorBuilder setMissing(Integer missing) {
        this.missing = missing;
        return this;
    }
}
