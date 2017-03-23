package ru.lucky.romans.campo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    ListView listView;
    List<Message> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = (ListView) findViewById(R.id.list_view);
        list = new ArrayList<>();

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, initData());

        new GetDialogs().execute();
        MessageAdapter messageAdapter = new MessageAdapter(this, initData());

        listView.setAdapter(messageAdapter);
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
            Log.e("JSON", jsonObject.toString());
            try {
                JSONArray chats = jsonObject.getJSONArray("items");
                for (int i = 0; i < chats.length(); i++) {
                    JSONObject currentChat = chats.getJSONObject(i);
                    list.add(new Message(1, currentChat.getString("name"), 1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
