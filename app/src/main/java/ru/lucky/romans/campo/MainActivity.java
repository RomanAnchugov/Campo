package ru.lucky.romans.campo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.lucky.romans.campo.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    TextView userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, 1);

        userId = (TextView) findViewById(R.id.test);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        CampoStats.ID_USER = data.getStringExtra("user_id");
        CampoStats.ACCESS_TOKEN = data.getStringExtra("access_token");
        new GetDialogs().execute();

    }


    private class GetDialogs extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                //основной запрос
                jsonObject = new JsonParser().getJsonFromUrl(Request.Messages.getDialogs(-10, -10, -10, -10, -10)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONArray dialogsArray = jsonObject.getJSONArray("items");
                    JSONObject currentTest = dialogsArray.getJSONObject(0);
                    userId.setText(Request.Messages.decrypt(currentTest.getString("body")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


