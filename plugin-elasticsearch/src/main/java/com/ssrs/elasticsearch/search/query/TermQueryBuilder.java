package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class TermQueryBuilder implements QueryBuilder {
	private final String name;
	private final JsonElement value;
	private int boost = 1;

	public TermQueryBuilder(String name, JsonElement value) {
		this.name = name;
		this.value = value;
	}

	public TermQueryBuilder boost(int boost) {
		this.boost = boost;
		return this;
	}

	@Override
	public JsonObject build() {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("missing property 'name'");
		}
		if (value == null) {
			throw new RuntimeException("missing property 'value'");
		}
		JsonObject term = object(name, value);
		if (boost != 1) {
			term.add("boost", new JsonPrimitive(boost));
		}
		return object("term", term);
	}
}
