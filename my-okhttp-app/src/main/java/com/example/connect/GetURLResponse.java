package com.example.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Calendar;
public class GetURLResponse {
    public String resultCode = "";
    public String resultDetailCode = "";
    public String url = "";
	
	public void deserialize (JSONObject jsonObj) throws JSONException {
		resultCode = (String) jsonObj.get("resultCode");
		resultDetailCode = (String) jsonObj.get("resultDetailCode");
		url = (String)jsonObj.getJSONObject("uploadUrl").getString("uploadUrl");
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
