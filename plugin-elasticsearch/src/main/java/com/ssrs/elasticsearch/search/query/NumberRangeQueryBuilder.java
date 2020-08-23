package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class NumberRangeQueryBuilder implements QueryBuilder{

    private boolean includeUpper;
    private Number to;
    private boolean includeLower;
    private Number from;
    private String fieldName;

    public NumberRangeQueryBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public NumberRangeQueryBuilder lt(Number to) {
        this.includeUpper = false;
        this.to = to;
        return this;
    }

    public NumberRangeQueryBuilder lte(Number to) {
        this.includeUpper = true;
        this.to = to;
        return this;
    }

    public NumberRangeQueryBuilder gt(Number from) {
        this.includeLower = false;
        this.from = from;
        return this;
    }

    public NumberRangeQueryBuilder gte(Number from) {
        this.includeLower = true;
        this.from = from;
        return this;
    }

    public JsonObject build() {
        if(from == null && to == null) {
            throw new RuntimeException("from and to fields are missing");
        }
        if(fieldName==null || fieldName.isEmpty()) {
            throw new RuntimeException("fieldName is missing");
        }
        JsonObject jsonObject = new JsonObject();
        JsonObject rangeObject = new JsonObject();
        jsonObject.add("range", rangeObject);
        JsonObject rangeParameters = new JsonObject();
        rangeObject.add(fieldName, rangeParameters);
        if(to != null) {
            rangeParameters.add((includeUpper ? "lte" : "lt"), new JsonPrimitive(to));
        }
        if(from != null) {
            rangeParameters.add((includeLower ? "gte" : "gt"), new JsonPrimitive(from));
        }
        return jsonObject;
    }
}
