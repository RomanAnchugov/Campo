package ru.lucky.romans.campo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeUserInformationActivity extends AppCompatActivity {

    EditText editUserName;
    EditText editUserSurname;
    EditText editUserPatronymic;
    Switch switchSex;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_information);

        editUserName = (EditText) findViewById(R.id.edit_user_name);
        editUserSurname = (EditText) findViewById(R.id.edit_user_surname);
        editUserPatronymic = (EditText) findViewById(R.id.edit_user_patronymic);
        switchSex = (Switch) findViewById(R.id.switch_user_sex);
        btnSubmit = (Button) findViewById(R.id.button_change_user_information_submit);

        new GetUserInformation().execute();
    }

    public void changeUserInformationSubmit(View v) {
        String firstName = editUserName.getText().toString();
        String surName = editUserSurname.getText().toString();
        String patronymic = editUserPatronymic.getText().toString();
        String sex = switchSex.isChecked() ? "2" : "1";
        new SaveUserInformation(firstName, surName, patronymic, sex).execute();
    }

    private class SaveUserInformation extends AsyncTask<String, String, JSONObject> {

        String firstName;
        String surName;
        String patronymic;
        String sex;

        public SaveUserInformation(String firstName, String surName, String patronymic, String sex) {
            this.firstName = firstName;
            this.surName = surName;
            this.patronymic = patronymic;
            this.sex = sex;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;
            jsonObject = new JsonParser().getJsonFromUrl(Request.Account.saveProfileInfo(surName, firstName, sex, null, patronymic));
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_LONG).show();
        }
    }

    private class GetUserInformation extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JsonParser().getJsonFromUrl(Request.Account.getProfileInfo()).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                editUserName.setText(jsonObject.getString("first_name"));
                editUserSurname.setText(jsonObject.getString("last_name"));
                editUserPatronymic.setText(jsonObject.getString("screen_name"));

                if (jsonObject.getInt("sex") == 1) {
                    switchSex.setChecked(false);
                } else {
                    switchSex.setChecked(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
