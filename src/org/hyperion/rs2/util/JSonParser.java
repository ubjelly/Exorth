package org.hyperion.rs2.util;

import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses a local json file.
 * @author Stephen
 */
public class JSonParser {
	public static JsonElement fromFile(InputStream location) {
		try {
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
			return parser.parse(new BufferedReader(new InputStreamReader(location)));
		} catch(Exception e) {}
		return null;
	}
}
