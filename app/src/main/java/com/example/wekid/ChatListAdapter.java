package com.example.wekid;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    // 아이템을 세트로 담기 위한 array
    private ArrayList<KidsItem> items = new ArrayList<>();

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        // 'chat_list_custom' 레이아웃을 infalte하여 convertView 참조 획득
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_list_custom, parent, false);
        }

        // 'chat_list_custom'에 정의된 위젯에 대한 참조 획득
        ImageView profileIcon = (ImageView) convertView.findViewById(R.id.profileIcon);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView contents = (TextView) convertView.findViewById(R.id.contents);

        // 각 리스트에 뿌려줄 아이템을 받아오는데 KidsItem 재활용
        KidsItem kidsItem = (KidsItem) getItem(position);

        // 각 위젯에 세팅된 아이템을 뿌려준다
        profileIcon.setImageDrawable(kidsItem.getIcon());
        name.setText(kidsItem.getName());
        contents.setText(kidsItem.getContents());

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(Drawable icon, String name, String contents) {
        KidsItem newItem = new KidsItem();

        // KidsItem에 아이템을 setting
        newItem.setIcon(icon);
        newItem.setName(name);
        newItem.setContents(contents);

        // items에 새로운 KidsItem을 추가
        items.add(newItem);
    }
}
