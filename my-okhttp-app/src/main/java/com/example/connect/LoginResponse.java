package com.example.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Calendar;

public class LoginResponse
{
	public String accessToken = "";
	public int expiresIn;
	public String tokenType = "Bearer";
	
	public void deserialize (JSONObject jsonObj) throws JSONException {
		accessToken = (String) jsonObj.get("access_token");
		expiresIn = (int) jsonObj.get("expires_in");
		tokenType = (String)jsonObj.get("token_type");
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
