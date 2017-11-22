package rostislav.weather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rostislav.weather.data.Data;
import rostislav.weather.data.Condition;
import rostislav.weather.data.Item;
import rostislav.weather.fragments.WeatherConditionFragment;
import rostislav.weather.listener.WeatherServiceListener;
import rostislav.weather.service.YahooWeatherService;

public class WeatherActivity extends AppCompatActivity implements WeatherServiceListener, LocationListener {

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;

    private ProgressDialog dialog;
    private YahooWeatherService service;
    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        service = new YahooWeatherService(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(46.9329791, 31.8679135, 1);

        String address = addresses.get(0).getAddressLine(0);
        service.refreshWeather("Odessa, Tx");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.show();

        service.setTemperatureUnit(preferences.getString("temperature_unit", null));
        String location = null;

        if (preferences.getBoolean("geolocation_enabled", true)) {
            getWeatherFromCurrentLocation();
        } else {
            location = preferences.getString("manual_location", null);
        }

        if (location != null) {
            service.refreshWeather(location);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.currentLocation:
                dialog.show();
                getWeatherFromCurrentLocation();
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getWeatherFromCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 0x00001);

            return;
        }

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }

        locationManager.requestSingleUpdate(locationCriteria, this, null);
    }

    @Override
    public void serviceSuccess(Data data) {
        dialog.hide();

        Item item = data.getItem();
        Condition[] forecast = data.getItem().getForecast();

        int resourceId = getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(), null, getPackageName());

        @SuppressWarnings("deprecation")
        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);

        weatherIconImageView.setImageDrawable(weatherIconDrawable);

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + data.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextView.setText(data.getLocation());

        for (int day = 0; day < forecast.length; day++) {
            if (day >= 10) {
                break;
            }

            Condition currentCondition = forecast[day];

            int viewId = getResources().getIdentifier("forecast_" + day, "id", getPackageName());
            WeatherConditionFragment fragment = (WeatherConditionFragment) getSupportFragmentManager().findFragmentById(viewId);

            if (fragment != null) {
                fragment.loadForecast(currentCondition, data.getUnits());
            }
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG);
    }

    @Override
    public void onLocationChanged(Location location) {
        List<Address> addresses;
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0);
            service.refreshWeather(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
