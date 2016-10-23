package ru.lucky.romans.campo;

import static ru.lucky.romans.campo.CampoStats.ACCESS_TOKEN;
import static ru.lucky.romans.campo.CampoStats.ID_USER;

/**
 * Created by Roman on 21.10.2016.
 */

public class Request {

    public static class Messages{
        public static String get(int out){
            return "method=messages.get&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&out=" + out;
        }
        public static String getById(int message_ids){
            return "method=messages.getById&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message_ids=" + message_ids;
        }

    }
    //TODO:   реализовать таким образом все методы

}
