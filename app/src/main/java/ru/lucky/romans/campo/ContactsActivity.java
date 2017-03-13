package ru.lucky.romans.campo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static ru.lucky.romans.campo.CampoStats.usersImages;

public class ContactsActivity extends AppCompatActivity {

    private LinearLayout friendsList;
    private FloatingActionButton btnAddNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        friendsList = (LinearLayout) findViewById(R.id.friends_list);
        btnAddNewContact = (FloatingActionButton) findViewById(R.id.btn_add_new_contact);

        new GetFriends().execute();
    }

    public void addNewContactClick(View v) {
        Intent intent = new Intent(getApplicationContext(), AddNewContactActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new GetFriends().execute();
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
            friendsList.removeAllViews();
            try {
                final JSONArray friendsJson = jsonObject.getJSONArray("items");
                for (int i = 0; i < friendsJson.length(); i++) {
                    final int finalI = i;

                    LinearLayout container = new LinearLayout(getApplicationContext());
                    LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final TextView friendName = new TextView(getApplicationContext());
                    final ImageView contactImage = new ImageView(getApplicationContext());

                    LinearLayout.LayoutParams contactImageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    contactImageParams.setMargins(5, 0, 15, 0);
                    new GetImage(CampoStats.IMAGE + friendsJson.getJSONObject(finalI).getString("photo_50"), contactImage, friendsJson.getJSONObject(finalI).getString("id"));

                    friendName.setTextColor(Color.BLACK);
                    friendName.setBackgroundColor(Color.LTGRAY);
                    friendName.setTextSize(24f);

                    friendName.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                try {
                                    Log.e("JSON", friendsJson.getJSONObject(finalI).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        }
                    });

                    friendName.setText(friendsJson.getJSONObject(i).getString("last_name") + " " + friendsJson.getJSONObject(i).getString("first_name") + "");
                    container.addView(contactImage, contactImageParams);
                    container.addView(friendName, layoutParams);

                    friendsList.addView(container, containerParams);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetImage extends AsyncTask<String, String, Bitmap> {

        private String src;
        private ImageView contactImage;
        private String userId;

        public GetImage(String src, ImageView contactImage, String userId) {
            this.src = src;
            this.contactImage = contactImage;
            this.userId = userId;

            if (!usersImages.containsKey(userId)) {
                this.execute();
            } else {
                Bitmap bitmap = usersImages.get(userId);
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
                contactImage.setImageBitmap(bitmap);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(src);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoInput(true);
            try {
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream input = null;
            try {
                input = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(input);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            contactImage.setImageBitmap(bitmap);
        }
    }
}

