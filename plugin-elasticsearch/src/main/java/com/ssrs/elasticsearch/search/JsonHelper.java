package com.ssrs.elasticsearch.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

import static java.util.Arrays.asList;

public class JsonHelper {

	public static final JsonObject object() {
		return new JsonObject();
	}

	public static final JsonObject object(String name, String value) {
		return object(name, new JsonPrimitive(value));
	}

	public static final JsonObject object(String name, boolean value) {
		return object(name, new JsonPrimitive(value));
	}

	public static final JsonObject object(String name, long value) {
		return object(name, new JsonPrimitive(value));
	}

	public static final JsonObject object(String name, JsonElement value) {
		JsonObject result = new JsonObject();
		result.add(name, value);
		return result;
	}

	public static final JsonObject object(String name1, JsonElement value1, String name2, JsonElement value2) {
		JsonObject result = new JsonObject();
		result.add(name1, value1);
		result.add(name2, value2);
		return result;
	}

	public static final JsonObject object(String name1, JsonElement value1, String name2, JsonElement value2, String name3, JsonElement value3) {
		JsonObject result = new JsonObject();
		result.add(name1, value1);
		result.add(name2, value2);
		result.add(name3, value3);
		return result;
	}
	
	public static final JsonObject object(String name1, JsonElement value1, String name2, JsonElement value2, String name3, JsonElement value3, String name4, JsonElement value4) {
		JsonObject result = new JsonObject();
		result.add(name1, value1);
		result.add(name2, value2);
		result.add(name3, value3);
		result.add(name4, value4);
		return result;
	}

	public static final JsonObject object(String name1, String value1, String name2, String value2) {
		JsonObject result = new JsonObject();
		result.add(name1, new JsonPrimitive(value1));
		result.add(name2, new JsonPrimitive(value2));
		return result;
	}

	public static final JsonObject object(String name1, String value1, String name2, String value2, String name3, String value3) {
		JsonObject result = new JsonObject();
		result.add(name1, new JsonPrimitive(value1));
		result.add(name2, new JsonPrimitive(value2));
		result.add(name3, new JsonPrimitive(value3));
		return result;
	}

	public static final JsonObject object(String name1, String value1, String name2, String value2, String name3, String value3, String name4, String value4) {
		JsonObject result = new JsonObject();
		result.add(name1, new JsonPrimitive(value1));
		result.add(name2, new JsonPrimitive(value2));
		result.add(name3, new JsonPrimitive(value3));
		result.add(name4, new JsonPrimitive(value4));
		return result;
	}

	public static final JsonObject object(String name1, String value1, String name2, String value2, String name3, String value3, String name4, String value4, String name5, String value5) {
		JsonObject result = new JsonObject();
		result.add(name1, new JsonPrimitive(value1));
		result.add(name2, new JsonPrimitive(value2));
		result.add(name3, new JsonPrimitive(value3));
		result.add(name4, new JsonPrimitive(value4));
		result.add(name5, new JsonPrimitive(value5));
		return result;
	}

	public static final JsonObject object(String name1, JsonElement value1, String name2, JsonElement value2, String name3, JsonElement value3, String name4, JsonElement value4, String name5,
			JsonElement value5) {
		JsonObject result = new JsonObject();
		result.add(name1, value1);
		result.add(name2, value2);
		result.add(name3, value3);
		result.add(name4, value4);
		result.add(name5, value5);
		return result;
	}

	public static final JsonObject object(String name1, String value1, String name2, String value2, String name3, String value3, String name4, String value4, String name5, String value5, String name6,
			String value6) {
		JsonObject result = new JsonObject();
		result.add(name1, new JsonPrimitive(value1));
		result.add(name2, new JsonPrimitive(value2));
		result.add(name3, new JsonPrimitive(value3));
		result.add(name4, new JsonPrimitive(value4));
		result.add(name5, new JsonPrimitive(value5));
		result.add(name6, new JsonPrimitive(value6));
		return result;
	}

	public static final JsonArray array(JsonElement... elements) {
		return array(asList(elements));
	}

	public static final JsonArray array(List<JsonElement> elements) {
		JsonArray jsonArray = new JsonArray();
		for (JsonElement element : elements) {
			jsonArray.add(element);
		}
		return jsonArray;
	}
	
	public static final JsonArray stringArray(String ...elements) {
		JsonArray jsonArray = new JsonArray();
		for (String value : elements) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return jsonArray;
	}
	
	public static final JsonArray stringArray(List<String> elements) {
		JsonArray jsonArray = new JsonArray();
		for (String value : elements) {
			jsonArray.add(new JsonPrimitive(value));
		}
		return jsonArray;
	}
}
