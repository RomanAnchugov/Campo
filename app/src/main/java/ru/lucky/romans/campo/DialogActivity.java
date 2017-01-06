package ru.lucky.romans.campo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.R.id.message;

public class DialogActivity extends AppCompatActivity {

    private String conversationId;
    private TextView textView;
    private LinearLayout linearLayoutContentMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        conversationId = intent.getStringExtra("conversation_id");
        linearLayoutContentMessages = (LinearLayout) findViewById(R.id.linear_layout_messages_content);
        new GetMessages().execute();

    }

    private class GetMessages extends AsyncTask<String, String, JSONObject>{

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject currentMessages = null;
            try {
                currentMessages = new JsonParser().getJsonFromUrl(Request.Messages.getHistory(conversationId, null, null, null, null)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return currentMessages;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONArray messagesArray = null;
            try {
                messagesArray = jsonObject.getJSONArray("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                for(int i = jsonObject.getInt("count"); i >= 0; i--){

                    //контейнер для сообщения с картинкой
                    RelativeLayout currentMessageContainer = new RelativeLayout(getApplicationContext());

                    //картинка пользователя
                    ImageView messageImage = new ImageView(getApplicationContext());

                    try {
                        new GetImage(CampoStats.IMAGE + messagesArray.getJSONObject(i).getString("u_photo_50"), messageImage).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //параментры для каждого сообщения
                    LinearLayout.LayoutParams paramsFlex = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //Параментры для сообщения
                    RelativeLayout.LayoutParams userMessageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //параментры для картинки сообщения
                    RelativeLayout.LayoutParams userImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    //текст сообщения
                    TextView currentMessage = new TextView(getApplicationContext());
                    messageImage.setId(i);

                    try {
                        if(messagesArray.getJSONObject(i).getString("uid").equals(CampoStats.ID_USER)){
                            currentMessageContainer.setBackgroundColor(Color.argb(255, 166, 218, 79));
                            currentMessage.setGravity(Gravity.END);
                            userImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                            userMessageParams.addRule(RelativeLayout.LEFT_OF, messageImage.getId());
                            userMessageParams.addRule(RelativeLayout.ALIGN_BASELINE, messageImage.getId());
                        }else{
                            currentMessageContainer.setBackgroundColor(Color.argb(255, 153, 203, 140));
                            currentMessage.setGravity(Gravity.START);

                            userMessageParams.addRule(RelativeLayout.RIGHT_OF, messageImage.getId());
                            userMessageParams.addRule(RelativeLayout.ALIGN_BASELINE, messageImage.getId());
                        }
                        currentMessage.setTextColor(Color.BLACK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        currentMessage.setText(Request.Messages.decrypt(messagesArray.getJSONObject(i).getString("body")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    currentMessageContainer.addView(currentMessage, userMessageParams);
                    currentMessageContainer.addView(messageImage, userImageParams);
                    linearLayoutContentMessages.addView(currentMessageContainer, paramsFlex);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class GetImage extends AsyncTask<String, String, Bitmap>{

        private String src;
        private ImageView dialogImage;

        public GetImage(String src, ImageView dialogImage){
            this.src = src;
            this.dialogImage = dialogImage;
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
            connection.setDoInput(true);;
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
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
            dialogImage.setImageBitmap(bitmap);
        }
    }
}
