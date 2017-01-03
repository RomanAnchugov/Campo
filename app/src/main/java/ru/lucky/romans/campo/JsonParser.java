package ru.lucky.romans.campo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Roman on 02.01.2017.
 */

public class JsonParser {

    HttpURLConnection connection = null;
    BufferedReader reader = null;
    JSONObject jsonObject;

    public JSONObject getJsonFromUrl(String request) {
        try {
            URL url = new URL(CampoStats.SERVER);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.connect();//сделать проверку на интеренет подключение

            //send data
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
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
            stream.close();
            jsonObject = new JSONObject(buffer.toString());
            return jsonObject;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
}
