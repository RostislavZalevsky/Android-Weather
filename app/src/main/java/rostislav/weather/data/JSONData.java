package rostislav.weather.data;

import org.json.JSONObject;

public interface JSONData {
    void JSONData(JSONObject data);
    JSONObject toJSONData();
}
