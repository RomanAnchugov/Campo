package ru.lucky.romans.campo.login;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ru.lucky.romans.campo.JsonParser;
import ru.lucky.romans.campo.R;
import ru.lucky.romans.campo.Request;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new JsonGetter().execute();
    }

    private class JsonGetter extends AsyncTask<String, String, JSONObject>{

        @Override
        protected void onPreExecute() {
            //считывание вьюшек
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return new JsonParser().getJsonFromUrl(Request.Messages.getHistory(1, -10, -10, -10, -10));
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //запись во вьшки
            try {
                Log.e("JSON", jsonObject.getString("responce"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
