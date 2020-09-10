package com.example.easyday.CLASS;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ParserJSonWeather {
    private String location,humidity,sunset,sunrise,time,temp,minTemp,maxTemp,wind,pressure,status;
    private Context context;
    private final static String API_WEATHER = "https://api.openweathermap.org/data/2.5/weather?q=";
    private final static String API_KEY_WEATHER = "cc3e96b3ac51918adf3f0270b9631079";
    private String TAG = "jsonweather";

    public String getTAG() {
        return TAG;
    }

    public static ParserJSonWeather getInstance()
    {
        return new ParserJSonWeather();
    }
    public ParserJSonWeather() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void processParser(final View view, final String city, final Weather weather)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final Weather finalWeather = weather;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrlApi(city), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject Weather = response.getJSONArray("weather").getJSONObject(0);
                    weather.setStatus(editStatusWeather(Weather.getString("description")));
                    Log.d(getTAG(), "weather: " + Weather.toString());

                    JSONObject main = response.getJSONObject("main");
                    Log.d(getTAG(), "main: " + main.toString());
                    weather.setTemp(main.getString("temp")+"°C");
                    weather.setMinTemp("Min Temp: "+ main.getString("temp_min")+" °C");
                    weather.setMaxTemp("Max Temp: "+main.getString("temp_max")+" °C");
                    weather.setPressure(main.getString("pressure"));
                    weather.setHumidity(main.getString("humidity"));

                    JSONObject wind = response.getJSONObject("wind");
                    Log.d(getTAG(),"wind: " + wind.toString());
                    weather.setWind(wind.getString("speed"));


                    Long time = response.getLong("dt");
                    String timeFormart = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.ENGLISH).format(new Date(time*1000));
                    weather.setTime(timeFormart);

                    JSONObject sys = response.getJSONObject("sys");
                    Log.d(getTAG(),"sys: " + sys.toString());
                    String sunrise = new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(new Date(sys.getLong("sunrise")*1000));
                    weather.setSunrise(sunrise);
                    String sunset = new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(new Date(sys.getLong("sunset")*1000));
                    weather.setSunset(sunset);
                    String location = city +", " + getCountry(sys.getString("country"));
                    weather.setLocation(location);

                    Log.d(getTAG(),"Weather Result: " + weather.toString());


                    view.findViewById(R.id.loader).setVisibility(View.GONE);
                    view.findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);



                } catch (JSONException e) {
                    view.findViewById(R.id.loader).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.errorText).setVisibility(View.VISIBLE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TOOL.setToast(context, "An occurred while Broadcast Weather....");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private String getUrlApi(String city)
    {
        String response = null;
        try {
            response = API_WEATHER+city+"&units=metric&appid="+API_KEY_WEATHER;
        }
        catch (Exception e)
        {
            response=null;
        }
        return response;
    }

    public static String editStatusWeather(String s)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(String item : s.split(" "))
        {
            char c = Character.toUpperCase(item.charAt(0));
            item = c+item.substring(1);
            stringBuilder.append(item+" ");
        }

        return stringBuilder.toString();
    }

    private String getCountry(String code2){
        Locale locale = new Locale("",code2);
        if(code2.equals("VN")) return "Viet Nam";
        else return locale.getDisplayCountry();
    }
}
