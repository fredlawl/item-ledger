package com.fredlawl.itemledger.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fredlawl.itemledger.entity.Character;

import java.util.List;

public class ChooseCharacterListAdapter extends ArrayAdapter<Character> {

    public ChooseCharacterListAdapter(Context context, List<Character> characterList) {
        super(context, android.R.layout.simple_list_item_1, characterList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Character character = getItem(position);
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView item = view.findViewById(android.R.id.text1);
        item.setText(character.getNamePart().toString());
        return view;
    }
}
