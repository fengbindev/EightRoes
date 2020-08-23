package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.array;


public class BoolQueryBuilder implements QueryBuilder {
    private JsonArray mustFilter = array();
    private JsonArray mustNotFilter = array();
    private JsonArray shouldFilter = array();
    private String minimumShouldMatch = null;

    @Override
    public JsonObject build() {
        if (mustFilter.size() == 0 && mustNotFilter.size() == 0 && shouldFilter.size() == 0) {
            throw new RuntimeException("mustFilter and mustNotFilter are empty");
        }
        JsonObject jsonObject = new JsonObject();
        JsonObject boolObject = new JsonObject();
        jsonObject.add("bool", boolObject);
        if (mustFilter.size() > 0) {
            if (mustFilter.size() == 1) {
                boolObject.add("must", mustFilter.get(0));
            } else {
                boolObject.add("must", mustFilter);
            }
        }
        if (mustNotFilter.size() > 0) {
            if (mustNotFilter.size() == 1) {
                boolObject.add("must_not", mustNotFilter.get(0));
            } else {
                boolObject.add("must_not", mustNotFilter);
            }
        }
        if (shouldFilter.size() > 0) {
            if (shouldFilter.size() == 1) {
                boolObject.add("should", shouldFilter.get(0));
            } else {
                boolObject.add("should", shouldFilter);
            }
        }

        if (minimumShouldMatch != null) {
            boolObject.add("minimum_should_match", new JsonPrimitive(minimumShouldMatch));
        }
        return jsonObject;
    }

    public boolean isEmpty() {
        return mustFilter.size() == 0 && mustNotFilter.size() == 0 && shouldFilter.size() == 0;
    }

    public BoolQueryBuilder must(JsonObject filter) {
        this.mustFilter.add(filter);
        return this;
    }

    public BoolQueryBuilder mustNot(JsonObject filter) {
        this.mustNotFilter.add(filter);
        return this;
    }

    public BoolQueryBuilder must(QueryBuilder queryBuilder) {
        must(queryBuilder.build());
        return this;
    }

    public BoolQueryBuilder should(QueryBuilder queryBuilder) {
        should(queryBuilder.build());
        return this;
    }

    public BoolQueryBuilder should(JsonObject shouldFilter) {
        this.shouldFilter.add(shouldFilter);
        return this;
    }

    public BoolQueryBuilder mustNot(QueryBuilder queryBuilder) {
        mustNot(queryBuilder.build());
        return this;
    }

    public BoolQueryBuilder minimumShouldMatch(String s) {
        this.minimumShouldMatch = s;
        return this;
    }
}
