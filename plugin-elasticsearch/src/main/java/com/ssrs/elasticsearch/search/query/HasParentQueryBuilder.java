package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class HasParentQueryBuilder implements QueryBuilder {
    private final String type;
    private final QueryBuilder query;

    public HasParentQueryBuilder(String type, QueryBuilder query) {
        this.type = type;
        this.query = query;
    }

    @Override
    public JsonObject build() {
        JsonObject hasParent = new JsonObject();
        hasParent.add("type", new JsonPrimitive(type));
        hasParent.add("query", query.build());
        return object("has_parent", hasParent);
    }
}
