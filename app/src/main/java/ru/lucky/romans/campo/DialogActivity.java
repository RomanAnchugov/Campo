package ru.lucky.romans.campo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class DialogActivity extends AppCompatActivity implements View.OnClickListener {

    private String conversationId;
    private LinearLayout linearLayoutContentMessages;
    private ImageButton sendButton;
    private EditText editMessage;
    private ScrollView messagesScroller;

    private boolean senderFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        sendButton = (ImageButton) findViewById(R.id.button_send);
        editMessage = (EditText) findViewById(R.id.edit_message);
        messagesScroller = (ScrollView) findViewById(R.id.scroll_messages);
        sendButton.setOnClickListener(this);
        conversationId = intent.getStringExtra("conversation_id");
        linearLayoutContentMessages = (LinearLayout) findViewById(R.id.linear_layout_messages_content);
        senderFlag = false;

        new GetMessages(20).execute();
        new GetChat().execute();//test
    }

    @Override
    public void onClick(View v) {
        if (!senderFlag) {
            new SentMessage(editMessage.getText().toString()).execute();
        }
    }

    private class SentMessage extends AsyncTask<String, String, JSONObject> {

        private String currentMessage;

        public SentMessage(String currentMessage) {
            this.currentMessage = currentMessage;
        }

        @Override
        protected void onPreExecute() {
            senderFlag = true;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            editMessage.setText("");
            new GetMessages(50).execute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responce = null;
            responce = new JsonParser().getJsonFromUrl(Request.Messages.send(conversationId, Request.Messages.encrypt(currentMessage), null));
            return responce;
        }
    }

    private class GetMessages extends AsyncTask<String, String, JSONObject> {

        private int messagesCount;

        public GetMessages(int messagesCount) {
            this.messagesCount = messagesCount;
            linearLayoutContentMessages.removeAllViews();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject currentMessages = null;
            try {
                currentMessages = new JsonParser().getJsonFromUrl(Request.Messages.getHistory(conversationId, null, null, null, messagesCount + "")).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return currentMessages;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            senderFlag = false;
            JSONArray messagesArray = null;
            Log.e("JSON", jsonObject.toString());
            try {
                if (jsonObject.getInt("count") != 0) {
                    try {
                        messagesArray = jsonObject.getJSONArray("items");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        int i;
                        if (messagesCount > jsonObject.getInt("count")) {
                            i = jsonObject.getInt("count");
                        } else {
                            i = messagesCount;
                        }
                        i = jsonObject.getInt("count");
                        for (; i >= 0; i--) {

                            //контейнер для сообщения с картинкой
                            RelativeLayout currentMessageContainer = new RelativeLayout(getApplicationContext());

                            //картинка пользователя
                            ImageView messageImage = new ImageView(getApplicationContext());

                            try {
                                new GetImage(CampoStats.IMAGE + messagesArray.getJSONObject(i).getString("u_photo_50"), messageImage, messagesArray.getJSONObject(i).getString("uid"));
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
                            if (i != 0) {
                                messageImage.setId(i);
                            } else {
                                messageImage.setId(jsonObject.getInt("count") + 1);
                            }

                            try {
                                if (messagesArray.getJSONObject(i).getString("uid").equals(CampoStats.ID_USER)) {
                                    currentMessageContainer.setBackgroundColor(Color.argb(255, 166, 218, 79));
                                    currentMessage.setGravity(Gravity.END);
                                    userImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    userImageParams.setMargins(15, 0, 0, 0);

                                    userMessageParams.addRule(RelativeLayout.LEFT_OF, messageImage.getId());
                                    userMessageParams.addRule(RelativeLayout.ALIGN_BASELINE, messageImage.getId());
                                } else {
                                    currentMessageContainer.setBackgroundColor(Color.argb(255, 153, 203, 140));
                                    currentMessage.setGravity(Gravity.START);
                                    userImageParams.setMargins(0, 0, 15, 0);

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
                            paramsFlex.setMargins(0, 0, 0, 1);
                            linearLayoutContentMessages.addView(currentMessageContainer, paramsFlex);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

    }

    private class GetImage extends AsyncTask<String, String, Bitmap>{

        private String src;
        private ImageView dialogImage;
        private String userId;
        private Bitmap bitmap;

        public GetImage(String src, ImageView dialogImage, String userId) {
            this.src = src;
            this.dialogImage = dialogImage;
            this.userId = userId;

            if (!usersImages.containsKey(userId)) {
                this.execute();
            } else {
                Bitmap currentUserImage = usersImages.get(userId);
                setImage(currentUserImage);
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
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            usersImages.put(userId, bitmap);
            setImage(bitmap);
        }

        private void setImage(Bitmap bitmap) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
            dialogImage.setImageBitmap(bitmap);
            //прокрутка вниз при открытии и отправке нового сообщения
            messagesScroller.post(new Runnable() {
                @Override
                public void run() {
                    messagesScroller.scrollTo(0, linearLayoutContentMessages.getHeight());
                }
            });
        }
    }

    private class GetChat extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject responce = null;
            responce = new JsonParser().getJsonFromUrl(Request.Messages.getChat(conversationId, null));
            return responce;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //Log.e("JSON", jsonObject.toString());
        }
    }
}
