package ru.lucky.romans.campo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static ru.lucky.romans.campo.CampoStats.dialogsImages;

public class TestActivity extends AppCompatActivity {

    ListView listView;
    List<Message> list;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = (ListView) findViewById(R.id.list_view);
        registerForContextMenu(listView);
        list = new ArrayList<>();

        new GetDialogs().execute();
        messageAdapter = new MessageAdapter(this, initData());

        listView.setAdapter(messageAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_view) {
            ListView listView = (ListView) v;
            AdapterView.AdapterContextMenuInfo listViewInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Message currentMessage = (Message) listView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_chat_menu, menu);
            Intent intent = new Intent();
            intent.putExtra("chatId", currentMessage.getChatId());
            menu.getItem(0).setIntent(intent);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_chat_menu_item) {
            Intent intent = item.getIntent();
            String chatId = intent.getStringExtra("chatId");
            //TODO deleting dialog
        }
        return true;
    }

    private List<Message> initData() {
        return list;
    }

    private class GetDialogs extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                //основной запрос
                jsonObject = new JsonParser().getJsonFromUrl(Request.Messages.getDialogs(-10, -10, -10, 100, -10)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray chats = jsonObject.getJSONArray("items");
                for (int i = 0; i < chats.length(); i++) {
                    JSONObject currentChat = chats.getJSONObject(i);
                    String chatImageLink = currentChat.getString("photo_50");
                    String chatName = currentChat.getString("name");
                    String chatPreview = Request.Messages.decrypt(currentChat.getString("body"));
                    String chatId = currentChat.getString("conversation_id");

                    new GetChatImage(chatImageLink, chatName, chatPreview, chatId);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class GetChatImage extends AsyncTask<String, String, Bitmap> {

        private String src;
        private String chatName;
        private String chatPreview;
        private String chatId;

        public GetChatImage(String src, String chatName, String chatPreview, String chatId) {
            this.src = src;
            this.chatName = chatName;
            this.chatPreview = chatPreview;
            this.chatId = chatId;

            if (!dialogsImages.containsKey(chatId)) {
                this.execute();
            } else {
                Bitmap bitmap = dialogsImages.get(chatId);
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
                list.add(new Message(bitmap, chatName, chatPreview, chatId));
                messageAdapter.notifyDataSetChanged();
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
            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
            dialogsImages.put(chatId, bitmap);
            list.add(new Message(bitmap, chatName, chatPreview, chatId));
            messageAdapter.notifyDataSetChanged();
        }
    }
}
