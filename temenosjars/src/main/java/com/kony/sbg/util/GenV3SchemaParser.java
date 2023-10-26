package com.kony.sbg.util;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

public class GenV3SchemaParser {

	private static GenV3SchemaParser parser;

	private JSONObject schema;

	private GenV3SchemaParser(String schema) {
		this.schema = new JSONObject(schema);
	}

	public static GenV3SchemaParser Initilize(String schema) {
		parser = new GenV3SchemaParser(schema);
		return parser;
	}

	public JSONObject parse(String baseRef, Map<String, String> template, Map<String, String> input, boolean debug) {
		String[] splits = baseRef.split("/");

		if (!template.containsKey(splits[2])) {
			return null;
		}
		if (template.containsKey(splits[2])) {
			if (template.get(splits[2]).compareTo("true") != 0) {
				return null;
			}
		}

		JSONObject jsonObject = this.schema.getJSONObject(splits[1]);
		JSONObject properties = jsonObject.getJSONObject(splits[2]).getJSONObject("properties");
		return parse(properties, template, input, splits[2], debug);
	}

	public JSONObject parse(JSONObject properties, Map<String, String> template, Map<String, String> input, String base,
			boolean debug) {
		JSONObject result = new JSONObject();

		Iterator<String> keys = properties.keys();
		while (keys.hasNext()) {
			String key = keys.next();

			if (properties.getJSONObject(key).has("$ref")) {
				String type = properties.getJSONObject(key).getString("$ref");
				result.put(key, parser.parse(type, template, input, debug));
			} else if (properties.getJSONObject(key).has("type")) {
				String ref = base + "." + key;
				if (input.containsKey(ref)) {
					String type = properties.getJSONObject(key).getString("type");
					if (type.compareTo("string") == 0) {
						addString(result, key, input.get(ref));
					} else if (type.compareTo("boolean") == 0) {
						addBoolean(result, key, type);
					}
				}
			}
		}

		return result;
	}

	private void addBoolean(JSONObject result, String key, String value) {
		if (value.compareTo("true") == 0) {
			result.put(key, true);
		} else if (value.compareTo("false") == 0) {
			result.put(key, false);
		} else {
			result.put(key, false);
		}
	}

	private void addString(JSONObject result, String key, String value) {
		if (value.compareTo("") != 0) {
			result.put(key, value);
		}
	}
}