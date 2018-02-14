/*package Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tests {

	public static void main(String[] args) throws JsonProcessingException{
//		String s = "{\"pill_x\": 5, \"pill_y\": 5, \"color\": 4, \"score\": 0.639, \"camera_id\": 1, \"pill_id\": 1024}";
//		Gson gson = new Gson();
//		Gson gson = new GsonBuilder().create();
////		gson.toJson(s, new FileWriter("file.json"));
//
//		Message m = gson.fromJson(s, Message.class);
//		System.out.println(m);
//		System.out.println(gson.toJson(s));
		MNM m = new MNM(2,2,2.0,2,2,2,1);
		ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(m);
        System.out.println(json);
	}

}
*/