package ru.lucky.romans.campo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import ru.lucky.romans.campo.login.LoginActivity;

import static ru.lucky.romans.campo.CampoStats.dialogsImages;

//СПИСОК ДИАЛОГОВ
public class MainActivity extends AppCompatActivity{

    //navigation drawer
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggleButton;
    private TextView userId;
    private LinearLayout linearLayout;
    private ScrollView dialogsScroller;
    private FloatingActionButton createNewDialogButton;//FloatinButton
    //request codes for activities
    private int loginRequest = 1;
    private int currentDialogRequest = 2;
    private int createDialogRequest = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        toggleButton = new ActionBarDrawerToggle(this, drawerLayout, R.string.toggle_button_open, R.string.toggle_button_close);

        drawerLayout.addDrawerListener(toggleButton);
        toggleButton.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.data_file_name), MODE_PRIVATE);
        String login = pref.getString("login", null);
        String password = pref.getString("password", null);
        String accessToken = pref.getString("token", null);
        String idUser = pref.getString("user_id", null);

        //проверка на логининг
        if(accessToken == null || password == null || login == null || idUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, loginRequest);
        }else{
            CampoStats.ID_USER = idUser;
            CampoStats.ACCESS_TOKEN = accessToken;
            new GetDialogs().execute();
        }

        createNewDialogButton = (FloatingActionButton) findViewById(R.id.button_create_new_dialog);
        dialogsScroller = (ScrollView) findViewById(R.id.scroll_dialogs);
        linearLayout = (LinearLayout) findViewById(R.id.dialogs);
    }

    public void createDialogClick(View v) {
        Intent intent = new Intent(this, CreateDialogActivity.class);
        startActivityForResult(intent, createDialogRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        linearLayout.removeAllViews();
        if (requestCode == loginRequest) {
            CampoStats.ID_USER = data.getStringExtra("user_id");
            CampoStats.ACCESS_TOKEN = data.getStringExtra("access_token");
        }
        new GetDialogs().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggleButton.onOptionsItemSelected(item);
    }

    private class GetDialogs extends AsyncTask<String, String, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                //основной запрос
                jsonObject = new JsonParser().getJsonFromUrl(Request.Messages.getDialogs(-10, -10, -10, -10, -10)).getJSONObject("responce");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONArray dialogsArray = jsonObject.getJSONArray("items");
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    for(int i = 0; i < dialogsArray.length(); i++){
                        final JSONObject currentDialog = dialogsArray.getJSONObject(i);

                        //контейнер для текущего диалога
                        final LinearLayout currentDialogContainer = new LinearLayout(getApplicationContext());
                        currentDialogContainer.setOrientation(LinearLayout.HORIZONTAL);
                        currentDialogContainer.setId(i);
                        currentDialogContainer.setClickable(true);


                        currentDialogContainer.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(event.getAction() == MotionEvent.ACTION_DOWN){
                                    currentDialogContainer.setBackgroundColor(Color.LTGRAY);
                                }
                                if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT || event.getAction() == MotionEvent.ACTION_CANCEL) {
                                    currentDialogContainer.setBackgroundColor(Color.TRANSPARENT);
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
                                    try {
                                        intent.putExtra("conversation_id", currentDialog.getString("conversation_id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivityForResult(intent, currentDialogRequest);

                                }
                                return true;
                            }
                        });

                        //картинка для диалога
                        LinearLayout.LayoutParams dialogImageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialogImageParams.setMargins(5, 0, 15, 0);
                        ImageView dialogImage = new ImageView(getApplicationContext());
                        new GetImage(CampoStats.IMAGE + currentDialog.getString("photo_50"), dialogImage, currentDialog.getString("conversation_id"));

                        //контейнер для боди и названия
                        LinearLayout bodyAndNameContainer = new LinearLayout(getApplicationContext());
                        bodyAndNameContainer.setOrientation(LinearLayout.VERTICAL);

                        //название диалога и привью
                        TextView bodyTextView = new TextView(getApplicationContext());
                        bodyTextView.setText(Request.Messages.decrypt(currentDialog.getString("body")));
                        bodyTextView.setTextSize(14);
                        bodyTextView.setTextColor(Color.argb(150, 0, 0, 0));
                        TextView dialogNameTextView = new TextView(getApplicationContext());
                        dialogNameTextView.setTextColor(Color.argb(200, 0, 0, 0));
                        dialogNameTextView.setTextSize(18);
                        dialogNameTextView.setText(currentDialog.getString("name"));

                        //линия между диалогами
                        View lineView = new View(getApplicationContext());
                        LinearLayout.LayoutParams lineViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lineViewParams.height = 2;
                        lineViewParams.setMargins(0, 0, 0, 15);
                        lineView.setBackgroundColor(Color.LTGRAY);

                        //доавбление в контейнер текущего диалога
                        currentDialogContainer.addView(dialogImage, dialogImageParams);
                        currentDialogContainer.addView(bodyAndNameContainer, layoutParams);

                        //добавление в контйнер для привью и навзания
                        bodyAndNameContainer.addView(dialogNameTextView, layoutParams);
                        bodyAndNameContainer.addView(bodyTextView, layoutParams);

                        LinearLayout.LayoutParams currentDialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        currentDialogParams.setMargins(0,15, 0, 0);
                        linearLayout.addView(currentDialogContainer, currentDialogParams);
                        linearLayout.addView(lineView, lineViewParams);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class GetImage extends AsyncTask<String, String, Bitmap>{

        private String src;
        private ImageView dialogImage;
        private String chatId;

        public GetImage(String src, ImageView dialogImage, String chatId) {
            this.src = src;
            this.dialogImage = dialogImage;
            this.chatId = chatId;

            if (!dialogsImages.containsKey(chatId)) {
                this.execute();
            } else {
                Bitmap bitmap = dialogsImages.get(chatId);
                bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
                dialogImage.setImageBitmap(bitmap);
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
            dialogImage.setImageBitmap(bitmap);
        }
    }

}


