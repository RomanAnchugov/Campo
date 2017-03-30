package ru.lucky.romans.campo.chats;

import android.graphics.Bitmap;

/**
 * Created by Roman on 23.03.2017.
 */

public class ChatTemplate {

    private Bitmap chatImage;
    private String chatName;
    private String chatPreview;
    private String chatId;

    public ChatTemplate(Bitmap chatImage, String chatName, String chatPreview, String chatId) {
        this.chatImage = chatImage;
        this.chatName = chatName;
        this.chatPreview = chatPreview;
        this.chatId = chatId;
    }

    public Bitmap getChatImage() {
        return chatImage;
    }

    public void setChatImage(Bitmap chatImage) {
        this.chatImage = chatImage;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatPreview() {
        return chatPreview;
    }

    public void setChatPreview(String chatPreview) {
        this.chatPreview = chatPreview;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
