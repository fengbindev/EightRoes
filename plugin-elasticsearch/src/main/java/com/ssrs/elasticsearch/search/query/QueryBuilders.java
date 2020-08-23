package com.ssrs.elasticsearch.search.query;

import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ssrs.elasticsearch.search.JsonHelper;


import java.util.List;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class QueryBuilders {
	
	public static QueryBuilder matchAll() {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject query = new JsonObject();
				query.add("match_all", new JsonObject());
				return query;
			}
		};
	}

	public static QueryBuilder matchAll(final QueryBuilder qb) {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject query = new JsonObject();
				query.add("query", qb.build());
				return query;
			}
		};
	}

	public static QueryBuilder multiMatchQuery(final String query, final String[] fields, final String operator, final String minimumShouldMatch) {
		return multiMatchQuery(query, fields, operator, null, minimumShouldMatch, null);
	}
	
	public static QueryBuilder multiMatchQuery(final String query, final String[] fields, final String operator, final String minimumShouldMatch, final String analyzer) {
		return multiMatchQuery(query, fields, operator, null, minimumShouldMatch, analyzer);
	}

	public static QueryBuilder multiMatchQuery(final String query, final String[] fields, final String operator, final String type, final String minimumShouldMatch, final String analyzer) {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject outerQuery = new JsonObject();
				JsonObject multiMatchQuery = new JsonObject();
				multiMatchQuery.add("query", new JsonPrimitive(query));
				multiMatchQuery.add("fields", JsonHelper.stringArray(fields));
				if (StrUtil.isNotEmpty(operator)) {
					multiMatchQuery.add("operator", new JsonPrimitive(operator));
				}
				if (StrUtil.isNotEmpty(type)) {
					multiMatchQuery.add("type", new JsonPrimitive(type));
				}
				if (StrUtil.isNotEmpty(minimumShouldMatch)) {
					multiMatchQuery.add("minimum_should_match", new JsonPrimitive(minimumShouldMatch));
				}
				if(StrUtil.isNotEmpty(analyzer)) {
					multiMatchQuery.add("analyzer", new JsonPrimitive(analyzer));
				}
				outerQuery.add("multi_match", multiMatchQuery);
				return outerQuery;
			}
		};
	}

	public static QueryBuilder filteredQuery(final QueryBuilder query, final JsonObject filter) {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject outerQuery = new JsonObject();
				JsonObject filtered = new JsonObject();
				outerQuery.add("bool", filtered);
				filtered.add("must", query.build());
				filtered.add("filter", filter);
				return outerQuery;
			}
		};
	}
	public static QueryBuilder prefixQuery(final String field, final String value) {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject outerQuery = new JsonObject();
				JsonObject queryObj = new JsonObject();
				JsonObject innerQuery = new JsonObject();
				queryObj.add("value", new JsonPrimitive(value));
				innerQuery.add(field, queryObj);
				outerQuery.add("prefix", innerQuery);
				return outerQuery;
			}
		};
	}

	public static QueryBuilder query(final QueryBuilder query) {
		return new QueryBuilder() {
			@Override
			public JsonObject build() {
				JsonObject search = new JsonObject();
				search.add("query", query.build());
				return search;
			}
		};
	}

	public static TermsQueryBuilder termsQuery(String name, JsonElement values) {
		return new TermsQueryBuilder(name, values);
	}

	public static TermsQueryBuilder termsQuery(String name, String... values) {
		JsonArray jsonArray = new JsonArray();
		for (String value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return new TermsQueryBuilder(name, jsonArray);
	}

	public static TermsQueryBuilder termsQuery(String name, Boolean... values) {
		JsonArray jsonArray = new JsonArray();
		for (Boolean value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return new TermsQueryBuilder(name, jsonArray);
	}

	public static TermsQueryBuilder termsQuery(String name, Number... values) {
		JsonArray jsonArray = new JsonArray();
		for (Number value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return new TermsQueryBuilder(name, jsonArray);
	}

	public static TermsQueryBuilder termsQuery(String name, List<String> values) {
		JsonArray jsonArray = new JsonArray();
		for (String value : values) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return new TermsQueryBuilder(name, jsonArray);
	}

	public static TermQueryBuilder termQuery(String name, JsonElement value) {
		return new TermQueryBuilder(name, value);
	}

	public static TermQueryBuilder termQuery(String name, String value) {
		return new TermQueryBuilder(name, new JsonPrimitive(value));
	}

	public static TermQueryBuilder termQuery(String name, Boolean value) {
		return new TermQueryBuilder(name, new JsonPrimitive(value));
	}

	public static TermQueryBuilder termQuery(String name, Number value) {
		return new TermQueryBuilder(name, new JsonPrimitive(value));
	}

	public static WildcardQueryBuilder wildcardQuery(String name, String value) {
		return wildcardQuery(name, new JsonPrimitive(value));
	}

	public static WildcardQueryBuilder wildcardQuery(String name, JsonElement value) {
		return new WildcardQueryBuilder(name, value);
	}

	public static RegexpQueryBuilder regexpQuery(String name, String value) {
		return regexpQuery(name, new JsonPrimitive(value));
	}

	public static RegexpQueryBuilder regexpQuery(String name, JsonElement value) {
		return new RegexpQueryBuilder(name, value);
	}

	public static BoolQueryBuilder bool() {
		return new BoolQueryBuilder();
	}

	public static QueryBuilder notQuery(final QueryBuilder nestedFilter) {
		return new QueryBuilder() {

			@Override
			public JsonObject build() {
				return object("not", nestedFilter.build());
			}
		};
	}

	public static QueryBuilder nestedQuery(final String path, final QueryBuilder queryBuilder) {
		return new QueryBuilder() {

			@Override
			public JsonObject build() {
				JsonObject jsonObject = new JsonObject();
				JsonObject nested = new JsonObject();
				nested.add("query", queryBuilder.build());
				nested.add("path", new JsonPrimitive(path));
				jsonObject.add("nested", nested);
				return jsonObject;
			}
		};
	}

	public static QueryBuilder prefixFilter(final String name, final String prefix) {
		return new QueryBuilder() {

			@Override
			public JsonObject build() {
				JsonObject jsonObject = new JsonObject();
				JsonObject value = new JsonObject();
				value.add(name, new JsonPrimitive(prefix));
				jsonObject.add("prefix", value);
				return jsonObject;
			}
		};
	}

	public static JsonObject existsFilter(String fieldName) {
		JsonObject jsonObject = new JsonObject();
		JsonObject existsObject = new JsonObject();
		jsonObject.add("exists", existsObject);
		existsObject.add("field", new JsonPrimitive(fieldName));
		return jsonObject;
	}

	public static AndQueryBuilder andQuery(QueryBuilder... queries) {
		return new AndQueryBuilder(queries);
	}

	public static AndQueryBuilder andQuery(List<QueryBuilder> queries) {
		return new AndQueryBuilder(queries);
	}

	public static NumberRangeQueryBuilder numberRangeFilter(String fieldName) {
		return new NumberRangeQueryBuilder(fieldName);
	}

	public static DateRangeQueryBuilder dateRangeFilter(String fieldName) {
		return new DateRangeQueryBuilder(fieldName);
	}

	public static HasParentQueryBuilder hasParent(String type, QueryBuilder query) {
		return new HasParentQueryBuilder(type, query);
	}

	public static FunctionScoreQueryBuilder functionScoreQuery(QueryBuilder innerQuery) {
		return new FunctionScoreQueryBuilder(innerQuery);
	}

	public static QueryBuilder boostingQuery(final QueryBuilder positive, final QueryBuilder negative, final double negativeBoost) {
		return new QueryBuilder() {

			@Override
			public JsonObject build() {
				return object("boosting", object("positive", positive.build(), "negative", negative.build(), "negative_boost", new JsonPrimitive(negativeBoost)));
			}
		};
	}

	public static FuzzyQueryBuilder fuzzyQuery(String fieldName, String value) {
		return new FuzzyQueryBuilder(fieldName, value);
	}

	public static GeoDistanceQueryBuilder geoDistanceQuery(String name) {
		return new GeoDistanceQueryBuilder(name);
	}
}
