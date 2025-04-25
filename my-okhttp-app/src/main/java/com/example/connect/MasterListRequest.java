package com.example.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Calendar;
public class MasterListRequest {
	public String machineId;
	
	public JSONObject serialize () {
		JSONObject obj = new JSONObject();
		try
		{
			obj.put ("machineId", this.machineId);
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
