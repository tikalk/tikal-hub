package com.tikalk.tikalhub.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tikalk.tikalhub.R;
import com.tikalk.tikalhub.model.FeedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpdatesListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    private final ArrayList<FeedItem> list = new ArrayList<FeedItem>();

    public UpdatesListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }

    public void addItems(List<FeedItem> items) {
        list.addAll(items);

        Collections.sort(list, new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem item1, FeedItem item2) {
                return item2.getDate().compareTo(item1.getDate());
            }
        });

        notifyDataSetChanged();
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

        if(view == null)
        view = inflater.inflate(R.layout.updates_feed_item, null);
        FeedItem feedItem = list.get(i);

        ((TextView)view.findViewById(R.id.message)).setText(feedItem.getMessage());

        ImageView imageView = (ImageView)view.findViewById(R.id.image);
        if(feedItem.getImageUrl() != null && !feedItem.getImageUrl().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(feedItem.getImageUrl(), imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return view;

    }
}
