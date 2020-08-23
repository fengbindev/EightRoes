package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import static java.util.Arrays.asList;

public class AndQueryBuilder implements QueryBuilder {

	private final List<QueryBuilder> queries;

	public AndQueryBuilder(QueryBuilder... queries) {
		this(asList(queries));
	}

	public AndQueryBuilder(List<QueryBuilder> queries) {
		this.queries = queries;
	}

	@Override
	public JsonObject build() {
		if (queries == null || queries.isEmpty()) {
			throw new RuntimeException("missing property 'queries'");
		}
		JsonObject jsonObject = new JsonObject();
		JsonArray ja = new JsonArray();
		for (QueryBuilder qb : queries) {
			ja.add(qb.build());
		}
		jsonObject.add("and", ja);
		return jsonObject;
	}
}
