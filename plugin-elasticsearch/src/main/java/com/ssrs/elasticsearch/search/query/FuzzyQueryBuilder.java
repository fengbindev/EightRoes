package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class FuzzyQueryBuilder implements QueryBuilder {
	private final String fieldName;
	private final String value;
	private int boost = 1;
	private int maxExpansions = 50;
	private Integer fuzziness = null;
	private int prefixLength = 0;


	public FuzzyQueryBuilder(String fieldName, String value) {
		this.fieldName = fieldName;
		this.value = value;
	}

	public FuzzyQueryBuilder boost(int boost) {
		this.boost = boost;
		return this;
	}

	public FuzzyQueryBuilder maxExpansions(int maxExpansions) {
		this.maxExpansions = maxExpansions;
		return this;
	}

	public FuzzyQueryBuilder prefixLength(int prefixLength) {
		this.prefixLength = prefixLength;
		return this;
	}

	public FuzzyQueryBuilder fuzziness(int fuzziness) {
		this.fuzziness = fuzziness; // der Wolf im Fuzzipelz, Wolf im Fuzzipelz!
		return this;
	}

	@Override
	public JsonObject build() {
		JsonObject options = object(
				"value", this.value
		);
		if(boost!=1) {
			options.add("boost", new JsonPrimitive(boost));
		}
		if(maxExpansions!=50) {
			options.add("max_expansions", new JsonPrimitive(maxExpansions));
		}
		if(fuzziness!=null) {
			options.add("fuzziness", new JsonPrimitive(fuzziness));
		}
		if(prefixLength!=0) {
			options.add("prefix_length", new JsonPrimitive(prefixLength));
		}
		return object("fuzzy", object(
				fieldName, options
		));
	}
}
