package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RegexpQueryBuilder implements QueryBuilder {

    private final String name;
    private final JsonElement value;

    public RegexpQueryBuilder(String name, JsonElement value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public JsonObject build() {
        if (name==null || name.isEmpty()) {
            throw new RuntimeException("missing property 'name'");
        }
        if (value == null) {
            throw new RuntimeException("missing property 'value'");
        }
        JsonObject jsonObject = new JsonObject();
        JsonObject term = new JsonObject();
        jsonObject.add("regexp", term);
        term.add(name, value);
        return jsonObject;
    }
}
