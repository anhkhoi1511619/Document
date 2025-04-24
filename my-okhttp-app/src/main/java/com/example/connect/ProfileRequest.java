package com.example.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Calendar;

public class ProfileRequest {
	public int id;
	public String machine;
	
	public JSONObject serialize () {
		JSONObject obj = new JSONObject();
		try
		{
			obj.put ("id", this.id);
			obj.put ("machine", this.machine);
		} catch (JSONException e) {
			throw new RuntimeException();
		}
		return obj;
	}
	
    public String toString() {
        String ret = "";
        for (Field field : this.getClass().getFields()) {
            try {
                Object val = field.get(this);
                String valStr = val != null ? val.toString() : "null";
                if (field.getType() == Calendar.class) {
                    valStr = "...";
                }
                ret += field.getName() + ": " + valStr + ",";
            } catch (Exception e) {
                continue;
            }
        }
        return ret;
    }
}
