package ru.lucky.romans.campo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateDialogActivity extends AppCompatActivity {

    LinearLayout friendsList;
    EditText dialogTitle;
    ArrayList<String> friendsToAdd;
    ImageButton createDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_dialog);
        createDialog = (ImageButton) findViewById(R.id.button_create_dialog);
        dialogTitle = (EditText) findViewById(R.id.edit_create_dialog_title);
        friendsList = (LinearLayout) findViewById(R.id.linear_layout_create_dialog_friends);
        friendsToAdd = new ArrayList<>();
        new GetFriends().execute();

        createDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateDialog(dialogTitle.getText().toString()).execute();//TODO сделать проверку на заполненный поля
            }
        });
    }

    private class GetFriends extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            //основной запрос
            try {
                jsonObject = new JsonParser().getJsonFromUrl(Request.Friends.get(null, null)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                final JSONArray friendsJson = jsonObject.getJSONArray("items");
                for (int i = 0; i < friendsJson.length(); i++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final TextView friendName = new TextView(getApplicationContext());
                    friendName.setTextColor(Color.BLACK);
                    friendName.setBackgroundColor(Color.TRANSPARENT);
                    final int finalI = i;
                    friendName.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                if (((ColorDrawable) friendName.getBackground()).getColor() == Color.LTGRAY) {
                                    friendName.setBackgroundColor(Color.TRANSPARENT);

                                    try {
                                        friendsToAdd.remove(friendsJson.getJSONObject(finalI).getString("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    friendName.setBackgroundColor(Color.LTGRAY);

                                    try {
                                        friendsToAdd.add(friendsJson.getJSONObject(finalI).getString("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            return true;
                        }
                    });
                    friendName.setText(friendsJson.getJSONObject(i).getString("last_name") + friendsJson.getJSONObject(i).getString("first_name") + "");
                    friendsList.addView(friendName, layoutParams);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CreateDialog extends AsyncTask<String, String, JSONObject> {

        String userIds = "";
        String dialogName = "";

        public CreateDialog(String dialogName) {
            this.dialogName = dialogName;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            for (int i = 0; i < friendsToAdd.size(); i++) {
                userIds += friendsToAdd.get(i) + ",";
            }
            userIds += CampoStats.ID_USER;
            //основной запрос
            jsonObject = new JsonParser().getJsonFromUrl(Request.Messages.createChat(userIds, dialogName));
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.e("JSON", jsonObject.toString());
        }
    }
}
