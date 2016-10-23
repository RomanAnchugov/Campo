package ru.lucky.romans.campo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Roman on 21.10.2016.
 */
public class JsonLoader extends AsyncTask<String, String, String> {

    HttpURLConnection connection = null;
    BufferedReader reader = null;
    TextView textView;
    String request;

    public JsonLoader(TextView textView, String request){
        this.textView = textView;
        this.request = request;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(CampoStats.SERVER);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.connect();

            //send data
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            Log.e("JSON", request);
            wr.write(request);
            wr.flush();

            //get date
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            Log.e("JSON", buffer.toString());
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        textView.setText(s);
    }
}
