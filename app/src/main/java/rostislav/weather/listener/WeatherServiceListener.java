package rostislav.weather.listener;

import rostislav.weather.data.Data;

public interface WeatherServiceListener {
    void serviceSuccess(Data data);
    void serviceFailure(Exception exception);
}
