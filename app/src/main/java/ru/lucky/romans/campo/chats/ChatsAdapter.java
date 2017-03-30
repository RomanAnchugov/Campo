package ru.lucky.romans.campo.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.lucky.romans.campo.R;

/**
 * Created by Roman on 23.03.2017.
 */

public class ChatsAdapter extends BaseAdapter {

    private List<ChatTemplate> list;
    private LayoutInflater layoutInflater;

    public ChatsAdapter(Context context, List<ChatTemplate> list) {
        this.list = list;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View contentView = view;
        if (contentView == null) {
            contentView = layoutInflater.inflate(R.layout.message_layout, viewGroup, false);
        }
        TextView chatName = (TextView) contentView.findViewById(R.id.chat_header);
        TextView chatPreview = (TextView) contentView.findViewById(R.id.chat_preview);
        ImageView chatImage = (ImageView) contentView.findViewById(R.id.chat_image);

        chatName.setText(getMessage(i).getChatName());
        chatPreview.setText(getMessage(i).getChatPreview());
        chatImage.setImageBitmap(getMessage(i).getChatImage());

        return contentView;
    }

    private ChatTemplate getMessage(int i) {
        return (ChatTemplate) getItem(i);
    }
}
