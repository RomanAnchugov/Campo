package ru.lucky.romans.campo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class AddNewContactActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        editText = (EditText) findViewById(R.id.edit_user_id_add);
        button = (Button) findViewById(R.id.btn_add_contact_id);
        activity = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddNewContact(editText.getText().toString()).execute();
            }
        });

        //new AddNewContact()
    }

    private class AddNewContact extends AsyncTask<String, String, JSONObject> {
        private String tid;

        public AddNewContact(String tid) {
            this.tid = tid;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObject = null;

            jsonObject = new JsonParser().getJsonFromUrl(Request.Friends.add(CampoStats.ID_USER + "", tid + ""));

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            activity.finish();
        }
    }
}
