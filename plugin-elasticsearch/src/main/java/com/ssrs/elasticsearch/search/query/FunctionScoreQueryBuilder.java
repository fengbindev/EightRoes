package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class FunctionScoreQueryBuilder implements QueryBuilder {

    private QueryBuilder innerQuery;
    private String scoreMode;
    private FieldValueFactorBuilder scoreFunction;
    private ScriptScoreBuilder scriptScore;

    public FunctionScoreQueryBuilder(QueryBuilder innerQuery) {
        this.innerQuery = innerQuery;
    }

    public FunctionScoreQueryBuilder setFieldValueFactor(FieldValueFactorBuilder scoreFunction) {
        this.scoreFunction = scoreFunction;
        return this;
    }
    
    public FunctionScoreQueryBuilder setScriptScore(ScriptScoreBuilder scriptScore) {
        this.scriptScore = scriptScore;
        return this;
    }

    public FunctionScoreQueryBuilder setScoreMode(String scoreMode) {
        this.scoreMode = scoreMode;
        return this;
    }

    @Override
    public JsonObject build() {
        JsonObject functionScore = new JsonObject();
        functionScore.add("query", innerQuery.build());
        JsonArray array = new JsonArray();
        if(scoreMode!=null) {
            functionScore.add("score_mode", new JsonPrimitive(scoreMode));
        }
        if(scoreFunction!=null) {
            array.add(scoreFunction.build());
            functionScore.add("functions", array);
        }
        if(scriptScore != null){
             array.add(scriptScore.build());
             functionScore.add("functions", array);
        }
        JsonObject result = object("function_score", functionScore);
        return result;
    }
}
