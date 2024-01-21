package com.example.clima;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String...urls) {
            StringBuilder result = new StringBuilder();
            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while((line = reader.readLine())!= null)
                {
                    result.append(line).append("\n");
                }
                return result.toString();
            }

            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("main");

                weatherInfo = weatherInfo.replace("temp", "Temperatura");
                weatherInfo = weatherInfo.replace("_min", " Mínima");
                weatherInfo = weatherInfo.replace("_max", " Máxima");
                weatherInfo = weatherInfo.replace("feels_like", "Sensação Térmica");
                weatherInfo = weatherInfo.replace("\"", " ");
                weatherInfo = weatherInfo.replace("{", "");
                weatherInfo = weatherInfo.replace("}", "");
                weatherInfo = weatherInfo.replace(",", "\n");
                weatherInfo = weatherInfo.replace("pressure", "Pressão Atmosférica");
                weatherInfo = weatherInfo.replace("sea_level", "Pressão (Mar)");
                weatherInfo = weatherInfo.replace("grnd_level", "Pressão (Solo)");
                weatherInfo = weatherInfo.replace("humidity", "Umidade");


                show.setText(weatherInfo);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.show);

        final String[] temp = {""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cidade = cityName.getText().toString();

                try {
                    if(cidade != null) {
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + cidade + "&lang=pt_br&appid=57e60b7d6964951df821a8cf1d11ddb0&units=metric";
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Insire uma cidade!", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(temp[0] == null)
                {
                    show.setText("Cidade não encontrada!");
                }
            }
        });

    }
}