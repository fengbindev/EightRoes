package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DateRangeQueryBuilder implements QueryBuilder {

	private boolean includeUpper;
	private String toDate;
	private boolean includeLower;
	private String fromDate;
	private String fieldName;

	public DateRangeQueryBuilder(String fieldName) {
		this.fieldName = fieldName;
	}

	public DateRangeQueryBuilder lt(String to) {
		this.includeUpper = false;
		this.toDate = to;
		return this;
	}

	public DateRangeQueryBuilder lte(String to) {
		this.includeUpper = true;
		this.toDate = to;
		return this;
	}

	public DateRangeQueryBuilder gt(String from) {
		this.includeLower = false;
		this.fromDate = from;
		return this;
	}

	public DateRangeQueryBuilder gte(String from) {
		this.includeLower = true;
		this.fromDate = from;
		return this;
	}

	public JsonObject build() {
		if (toDate == null && fromDate == null) {
			throw new RuntimeException("from and to fields are missing");
		}
		if (fieldName == null || fieldName.isEmpty()) {
			throw new RuntimeException("fieldName is missing");
		}
		JsonObject jsonObject = new JsonObject();
		JsonObject rangeObject = new JsonObject();
		jsonObject.add("range", rangeObject);
		JsonObject rangeParameters = new JsonObject();
		rangeObject.add(fieldName, rangeParameters);
		if (toDate != null) {
			rangeParameters.add((includeUpper ? "lte" : "lt"), new JsonPrimitive(toDate));
		}
		if (fromDate != null) {
			rangeParameters.add((includeLower ? "gte" : "gt"), new JsonPrimitive(fromDate));
		}
		return jsonObject;
	}
}
