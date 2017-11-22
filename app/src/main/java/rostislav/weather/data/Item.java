package rostislav.weather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item implements JSONData {
    private Condition condition;
    private Condition[] forecast;

    public Condition getCondition() {
        return condition;
    }
    public Condition[] getForecast() {
        return forecast;
    }

    @Override
    public void JSONData(JSONObject data) {
        condition = new Condition();
        condition.JSONData(data.optJSONObject("condition"));

        JSONArray forecastData = data.optJSONArray("forecast");

        forecast = new Condition[forecastData.length()];

        for (int i = 0; i < forecastData.length(); i++) {
            forecast[i] = new Condition();
            try {
                forecast[i].JSONData(forecastData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject toJSONData() {
        JSONObject data = new JSONObject();
        try {
            data.put("condition", condition.toJSONData());
            data.put("forecast", new JSONArray(forecast));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
