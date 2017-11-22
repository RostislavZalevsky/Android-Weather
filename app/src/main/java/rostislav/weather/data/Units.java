package rostislav.weather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Units implements JSONData {
    private String temperature;
    public String getTemperature() {
        return temperature;
    }

    @Override
    public void JSONData(JSONObject data) {
        temperature = data.optString("temperature");
    }

    @Override
    public JSONObject toJSONData() {
        JSONObject data = new JSONObject();

        try {
            data.put("temperature", temperature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
