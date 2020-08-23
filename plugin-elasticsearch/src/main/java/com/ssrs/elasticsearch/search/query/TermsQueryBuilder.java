package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public class TermsQueryBuilder implements QueryBuilder{
    private final String name;
    private final JsonElement terms;

    public TermsQueryBuilder(String name, List<String> terms) {
        this.name = name;
        if (terms == null || terms.isEmpty()) {
            throw new RuntimeException("missing property 'terms'");
        }
        JsonArray jsonArray = new JsonArray();
        for (String term : terms) {
			jsonArray.add(new JsonPrimitive(term));
		}
        this.terms = jsonArray;
    }

    public TermsQueryBuilder(String name, JsonElement terms) {
        this.name = name;
        this.terms = terms;
    }

    @Override
    public JsonObject build() {
        if (name==null || name.isEmpty()) {
            throw new RuntimeException("missing property 'name'");
        }
        if (terms == null) {
            throw new RuntimeException("missing property 'terms'");
        }
        JsonObject jsonObject = new JsonObject();
        JsonObject termsObject = new JsonObject();
        jsonObject.add("terms", termsObject);
        termsObject.add(name, terms);
        return jsonObject;
    }
}
