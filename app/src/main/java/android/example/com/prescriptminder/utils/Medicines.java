package android.example.com.prescriptminder.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Medicines {

    private String name;
    private String note;
    private String details;

    public Medicines(String json) {
        try {
            parseJSON(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getDetails() {
        return details;
    }
}
