package ru.lucky.romans.campo;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Roman on 21.10.2016.
 */

public class CampoStats {
    public static String SERVER = "http://campo.slezins.ru/method/index.php";
    public static String IMAGE = "http://campo.slezins.ru/";
    public static String ACCESS_TOKEN = "1";
    public static String ID_USER = "1";
    public static Map<String, Bitmap> usersImages = new HashMap<>();
    public static Map<String, Bitmap> dialogsImages = new HashMap<>();
}

