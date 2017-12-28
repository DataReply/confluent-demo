package Util;

import com.google.gson.Gson;

public class JsonParser {
	public static MNM parse(String jsonString) {
		
		Gson g = new Gson();
		MNM m = g.fromJson(jsonString, MNM.class);
		return m;
	}

}
