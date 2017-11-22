package rostislav.weather.data;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationResult implements JSONData {

    private String address;
    public String getAddress() {
        return address;
    }

    @Override
    public void JSONData(JSONObject data) {
        address = data.optString("formatted_address");
    }

    @Override
    public JSONObject toJSONData() {
        JSONObject data = new JSONObject();

        try {
            data.put("formatted_address", address);
        } catch (JSONException e) {}

        return data;
    }
}
