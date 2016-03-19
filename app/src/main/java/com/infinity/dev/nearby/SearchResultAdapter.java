package com.infinity.dev.nearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchResultAdapter extends BaseAdapter {

    Context context;
    List<SearchItemBean> items;
    LayoutInflater inflater;

    public SearchResultAdapter(Context context, List<SearchItemBean> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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
        if(convertView == null)
            convertView = inflater.inflate(R.layout.auto_complete_list_item, parent, false);

        TextView searchResult = (TextView)convertView.findViewById(R.id.place_auto_complete_desc);
        searchResult.setText(items.get(position).getName());
        return convertView;
    }
}
