package ru.lucky.romans.campo;

import static ru.lucky.romans.campo.CampoStats.ACCESS_TOKEN;
import static ru.lucky.romans.campo.CampoStats.ID_USER;

/**
 * Created by Roman on 21.10.2016.
 */

public class Request {

    public static class Messages{
        public static String encrypt(String message){
            String crypt = "4bAjVxy";
            String responce = "";
            String temp="";
            char symbol;
            for(int i=0, k=0;i<message.length();i++){
                symbol=(char)(crypt.charAt(i-k*crypt.length())^message.charAt(i));
                temp+=symbol;
                if((i+1)%crypt.length()==0){
                    k++;
                    responce+=temp;
                    crypt=temp;
                    temp="";
                }
            }
            if(temp!="")responce+=temp;
            for (int i = 0; i < responce.length(); i++) {
                if (responce.charAt(i) == '\\') {
                    responce = responce.substring(0, i) + "\\" + responce.substring(i, responce.length());
                    i += 2;
                }

            }
            return responce;
        }
        public static String decrypt(String message){
            String crypt="4bAjVxy";
            String responce="";
            for(int i=0,k=0;i<message.length();i++){
                responce+=(char)(crypt.charAt(i-k*crypt.length())^message.charAt(i));
                if((i+1)%crypt.length()==0){
                    crypt=message.substring(k*crypt.length(),(k+1)*crypt.length());
                    k++;
                }
            }
            return responce;
        }
        public static String get(int out, int time_offset, int last_message_id, int offset, int count, int filters, int preview_length){
            String req = "method=messages.get&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&out=" + out;
            if(time_offset != -10){
                req += "&last_message_id=" + last_message_id;
            }
            if(offset != -10){
                req += "&offset=" + offset;
            }
            if(count != -10){
                req += "&count=" + count;
            }
            if(filters != -10){
                req +=  "&filters=" + filters;
            }
            if(preview_length != -10){
                req += "&preview_length=" + preview_length;
            }
            return req;
        }
        public static String getById(int message_ids, int preview_length){
            String req = "method=messages.getById&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message_ids=" + message_ids;
            if(preview_length != -10){
                req += "&preview_length=" + preview_length;
            }
            return req;
        }
        public static String getDialogs(int start_message_id, int preview_length, int offset, int count, int unread){
            String req = "method=messages.getDialogs&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER;
            if(start_message_id != -10){
                req += "&start_message_id=" + start_message_id;
            }
            if(preview_length != -10){
                req += "&preview_length=" + preview_length;
            }
            if(offset != -10){
                req += "&offset=" + offset;
            }
            if(count != -10){
                req += "&count=" + count;
            }
            if(unread != -10){
                req += "&unread=" + unread;
            }
            return req;
        }
        public static String getHistory(String id_conversation, String start_message_id, String rev, String offset, String count){
            String req = "method=messages.getHistory&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&id_conversation=" + id_conversation;
            if(start_message_id != null){
                req += "&start_message_id=" + start_message_id;
            }
            if(rev != null){
                req += "&rev=" + rev;
            }
            if(offset != null){
                req += "&offset=" + offset;
            }
            if(count != null){
                req += "&count=" + count;
            }
            return req;
        }

        public static String send(String id_conversation, String message, String forward_messages) {
            String req = "method=messages.send&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message=" + message + "&id_conversation=" + id_conversation;
            if(forward_messages != null){
                req += "&forward_messages=" + forward_messages;
            }
            return req;
        }
        public static String delete(String message_ids, int spam){
            String req = "method=messages.delete&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message_ids=" + message_ids;
            if(spam != -10){
                req += "&spam=" + spam;
            }
            return req;
        }
        public static String deleteDialog(int peer_id, int offset, int count){
            String req = "method=messages.deleteDialog&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&peer_id=" + peer_id;
            if(offset != -10){
                req += "&offset=" + offset;
            }
            if(count != -10){
                req += "&count=" + count;
            }
            return req;
        }
        public static String markAsRead(String message_ids){
            String req = "method=messages.markAsReadId&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message_ids=" + message_ids;
            return req;
        }
        public static String markAsImportant(String message_ids, int important){
            String req = "method=messages.markAsImportant&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&message_ids=" + message_ids + "&important=" + important;
            return req;
        }
        public static String getChat(String chat_ids, String fields){
            String req = "method=messages.getChat&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&chat_ids=" + chat_ids;
            if(fields != null){
                req += "&fields=" + fields;
            }
            return req;
        }

        public static String createChat(String user_ids, String title) {
            String req = "method=messages.createChat&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&user_ids=" + user_ids + "&title=" + title;
            return  req;
        }
        public static String editChat(int chat_id, String title){
            String req = "method=messages.editChat&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&chat_id=" + chat_id + "&title=" + title;
            return req;
        }
        public static String addChatUser(int chat_id, int user_id){
            String req = "method=messages.addChatUser&access_token=" + ACCESS_TOKEN + "&chat_id=" + chat_id + "&user_id=" + user_id;
            return req;
        }
        public static String getChatUsers(String chat_ids, String fields){
            String req = "method=messages.getChatUsers&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&chat_ids=" + chat_ids;
            if(fields != null){
                req += "&fields=" + fields;
            }
            return req;
        }
        public static String removeChatUser(int chat_id,int user_id){
            String req = "method=messages.removeChatUser&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&chat_id=" + chat_id + "&user_id=" + user_id;
            return req;
        }
        public static String setActivity(int type, int peer_id){
            String req = "method=messages.editChat&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&type=" + type + "&peer_id=" + peer_id;
            return req;
        }
    }

    public static class Account{
        public static String setOnline(){
            String req = "method=account.setOnline&access_token=" + ACCESS_TOKEN;
            return req;
        }
        public static String setOffline(){
            String req = "method=account.setOffline&access_token=" + ACCESS_TOKEN;
            return req;
        }
        public static String lookUpContacts(String phone_number){
            String req = "method=account.looUpContacts&access_token=" + ACCESS_TOKEN + "&phone_number=" + phone_number;
            return req;
        }
        public static String banUser(int sid, int tid){
            String req = "method=account.banUser&access_token=" + ACCESS_TOKEN + "&sid=" + sid + "&tid=" + tid;
            return req;
        }
        public static String unBanUser(int sid, int tid, int responce){
            String req = "method=account.unBanUser&access_token=" + ACCESS_TOKEN + "&sid=" + sid + "&tid=" + tid + "&responce=" + responce;
            return req;
        }
        public static String getBanned(int offset, int count){
            String req = "method=account.getBanned&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER;
            if(offset != -10){
                req += "&offset=" + offset;
            }
            if(count != -10){
                req += "&count=" + count;
            }
            return req;
        }
        public static String getProfileInfo(){
            String req = "method=account.getProfileInfo&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER;
            return req;
        }
        public static String saveProfileInfo(String last_name, String first_name, int sex, String screen_name, String maiden_name){
            String req = "method=account.saveProfileInfo&access_token=" + ACCESS_TOKEN + "&id_user=" + ID_USER + "&last_name=" + last_name + "&first_name=" + first_name + "&sex=" + sex + "&screen_name=" + screen_name + "&maiden_name=" + maiden_name;
            return req;
        }
    }

    public static class Friends{
        public static String add(String sid, String tid) {
            String req = "method=contacts.add&access_token=" + ACCESS_TOKEN + "&sid=" + sid + "&tid=" + tid;
            return req;
        }
        public static String delete(int sid, int tid){
            String req = "method=contacts.delete&access_token=" + ACCESS_TOKEN + "&sid=" + sid + "&tid=" + tid;
            return req;
        }

        public static String get(String offset, String count) {
            String req = "method=contacts.get&access_token=" + ACCESS_TOKEN;
            if (offset != null) {
                req += req + "&offset=" + offset;
            }
            if (count != null) {
                req += req + "&count=" + count;
            }
            return req;
        }
    }

    public static class Auth{
        public static String login(String login, String password){
            //	login(login, password, platformm, device)
            String req = "method=auth.login&login=" + login + "&password=" + password + "&platform=android&device=android";
            return req;
        }
    }

    //TODO:   реализовать таким образом все методы

}
