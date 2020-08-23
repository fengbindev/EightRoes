package com.ssrs.elasticsearch.search.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.ssrs.elasticsearch.search.JsonHelper.object;


public class GeoDistanceQueryBuilder implements QueryBuilder {

	private double lat;
	private double lon;
	private String distance;
	private String fieldName;

	public GeoDistanceQueryBuilder(String fieldName) {
		this.fieldName = fieldName;
	}

	public GeoDistanceQueryBuilder setLat(double lat) {
		this.lat = lat;
		return this;
	}

	public GeoDistanceQueryBuilder setLon(double lon) {
		this.lon = lon;
		return this;
	}

	public GeoDistanceQueryBuilder setDistance(String distance) {
		this.distance = distance;
		return this;
	}

	@Override
	public JsonObject build() {
		return object("geo_distance",
				object(
						"distance", new JsonPrimitive(distance),
						fieldName, object(
								"lat", new JsonPrimitive(lat),
								"lon", new JsonPrimitive(lon)
						)
				)
		);
	}
}
