package ru.lucky.romans.campo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Context context;

    private EditText loginField;
    private EditText passwordField;
    private Button loginButton;

    private String login;
    private String password;
    private String token;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        pref = getApplicationContext().getSharedPreferences(getString(R.string.data_file_name), MODE_PRIVATE);

        loginField = (EditText) findViewById(R.id.editTextLogin);
        passwordField = (EditText) findViewById(R.id.editTextPassword);
        loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);

        //SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.data_file_name), MODE_PRIVATE);
        String login = pref.getString("login", null);
        String password = pref.getString("password", null);
        String accessToken = pref.getString("token", null);
        String idUser = pref.getString("user_id", null);

        //проверка на логининг
        if (login != null && password != null && accessToken != null && idUser != null) {
            CampoStats.ID_USER = idUser;
            CampoStats.ACCESS_TOKEN = accessToken;
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Enter your account", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        login = loginField.getText().toString();
        password = passwordField.getText().toString();
        new JsonGetter().execute();
    }

    private class JsonGetter extends AsyncTask<String, String, JSONObject>{


        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JsonParser().getJsonFromUrl(Request.Auth.login(login, password)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null) {
                try {
                    //TODO сделать проверку на правильность пароль\логина
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    SharedPreferences.Editor editPref = pref.edit();
                    editPref.putString("login", login);
                    editPref.putString("password", password);
                    editPref.putString("token", jsonObject.getString("access_token"));
                    editPref.putString("user_id", jsonObject.getString("user_id"));
                    editPref.apply();

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
