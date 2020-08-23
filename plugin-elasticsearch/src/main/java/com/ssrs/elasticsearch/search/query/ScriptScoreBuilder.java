package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ScriptScoreBuilder implements QueryBuilder {

	private String inline;
	private String lang = "painless";
	private JsonObject params = new JsonObject();

	public void setInline(String inline) {
		this.inline = inline;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void addParam(String key, Boolean value) {
		params.add(key, new JsonPrimitive(value));
	}

	public void addParam(String key, Number value) {
		params.add(key, new JsonPrimitive(value));
	}

	public void addParam(String key, String value) {
		params.add(key, new JsonPrimitive(value));
	}

	@Override
	public JsonObject build() {
		JsonObject outer = new JsonObject();
		JsonObject scriptScore = new JsonObject();
		JsonObject script = new JsonObject();
		outer.add("script_score", scriptScore);
		scriptScore.add("script", script);
		script.add("lang", new JsonPrimitive(lang));
		script.add("params", params);
		script.add("inline", new JsonPrimitive(inline));
		return outer;
	}

}
