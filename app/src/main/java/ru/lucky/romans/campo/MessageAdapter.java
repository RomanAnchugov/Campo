package ru.lucky.romans.campo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Roman on 23.03.2017.
 */

public class MessageAdapter extends BaseAdapter {

    private List<Message> list;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Context context, List<Message> list) {
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
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View contentView = view;
        if (contentView == null) {
            contentView = layoutInflater.inflate(R.layout.message_layout, viewGroup, false);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.textView6);
        textView.setText(getMessage(i).getName());
        return contentView;
    }

    private Message getMessage(int i) {
        return (Message) getItem(i);
    }
}
