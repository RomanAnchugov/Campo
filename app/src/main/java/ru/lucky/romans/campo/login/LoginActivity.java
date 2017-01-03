package ru.lucky.romans.campo.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import ru.lucky.romans.campo.CampoStats;
import ru.lucky.romans.campo.JsonParser;
import ru.lucky.romans.campo.MainActivity;
import ru.lucky.romans.campo.R;
import ru.lucky.romans.campo.Request;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Context context;

    private EditText loginField;
    private EditText passwordField;
    private Button loginButton;

    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.data_file_name), MODE_PRIVATE);
        login = pref.getString("login", null);
        password = pref.getString("password", null);

        loginField = (EditText) findViewById(R.id.editTextLogin);
        passwordField = (EditText) findViewById(R.id.editTextPassword);
        loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(this);

        if(login != null && password != null){
            loginField.setText(login);
            passwordField.setText(password);
        }

    }

    @Override
    public void onClick(View v) {
        new JsonGetter().execute();
        login = loginField.getText().toString();
        password = passwordField.getText().toString();
    }

    private class JsonGetter extends AsyncTask<String, String, JSONObject>{

        @Override
        protected void onPreExecute() {
        }

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
                    //TODO сдулать проверку на правильность пароль\логина
                    Intent intent = new Intent();
                    intent.putExtra("user_id", jsonObject.getString("user_id"));
                    intent.putExtra("access_token", jsonObject.getString("access_token"));
                    setResult(RESULT_OK, intent);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.data_file_name), MODE_PRIVATE);
                    SharedPreferences.Editor editPref = pref.edit();
                    editPref.putString("login", login);
                    editPref.putString("password", password);
                    editPref.apply();

                    Log.e("JSON", jsonObject.toString());
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
