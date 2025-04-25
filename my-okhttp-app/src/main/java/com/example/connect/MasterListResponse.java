package com.example.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.ArrayList;

public class MasterListResponse {
    public class Version {
        public String id;
        public String version;
        public String url;
    }
    public String resultCode = "";
    public String resultDetailCode = "";
    public ArrayList<Version> masterVersion = new ArrayList<Version>();
	
	public void deserialize (JSONObject jsonObj) throws JSONException {
		resultCode = (String) jsonObj.get("resultCode");
		resultDetailCode = (String) jsonObj.get("resultDetailCode");
        masterVersion.clear();
        if(jsonObj.isNull("outputMasterVerList")) {
            return;
        }
        var versions = jsonObj.getJSONArray("outputMasterVerList");
        for (int i = 0; i < versions.length(); i++) {
            Version v = new Version();
            var obj = versions.getJSONObject(i);
            v.id = obj.getString("id");
            v.version = obj.getString("version");
            v.url = obj.isNull("url")?null:obj.getString("url");
            masterVersion.add(v);
        }
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
