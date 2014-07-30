package com.tikalk.tikalhub.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tikalk.tikalhub.R;
import com.tikalk.tikalhub.model.FeedAggregator;
import com.tikalk.tikalhub.model.FeedItem;
import com.tikalk.tikalhub.model.FetchItemsCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpdatesListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .delayBeforeLoading(600)
            .imageScaleType(ImageScaleType.NONE)
            .build();


    private final ArrayList<FeedItem> list = new ArrayList<FeedItem>();
    private final int density;
    private final int maxImageWidth;
    private final Point initialImageSize;
    private final DateFormat dateFormat;
    private Object loadContext;

    public UpdatesListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.dateFormat = android.text.format.DateFormat.getDateFormat(context);

        this.density = context.getResources().getDisplayMetrics().densityDpi;
        this.maxImageWidth = (300 * density) / 160; // 300dp
        this.initialImageSize = new Point((130 * density) / 160, (100 * density) / 160); // 130pt x 100pt

    }

    private void addItems(List<FeedItem> items) {
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

        if (view == null)
            view = inflater.inflate(R.layout.updates_feed_item, null);
        final FeedItem feedItem = list.get(i);

        ((TextView) view.findViewById(R.id.message)).setText(feedItem.getMessage());
        ((TextView) view.findViewById(R.id.date)).setText(dateFormat.format(feedItem.getDate()));

        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        if (feedItem.getImageUrl() != null && !feedItem.getImageUrl().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            Point imageSize = feedItem.getImageSize();

            if (imageSize == null) {
                imageSize = initialImageSize;
            }
            setImageSize(imageView, imageSize);

            ImageLoader.getInstance().displayImage(feedItem.getImageUrl(), imageView, displayImageOptions, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    bitmap.setDensity(160); // pixel ration 1:1
                    int width = bitmap.getScaledWidth(density);
                    int height = bitmap.getScaledHeight(density);

                    Point imageSize = width > maxImageWidth
                            ? new Point(maxImageWidth, (height * maxImageWidth) / width)
                            : new Point(width, height);

                    feedItem.setImageSize(imageSize);
                    setImageSize(imageView, imageSize);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }

        return view;

    }

    private static void setImageSize(ImageView imageView, Point point) {
        android.view.ViewGroup.LayoutParams layout = imageView.getLayoutParams();

        if (layout.width != point.x || layout.height != point.y) {
            layout.width = point.x;
            layout.height = point.y;
            imageView.setLayoutParams(layout);
        }
    }

    public void load(boolean refresh) {
        list.clear();
        notifyDataSetChanged();

        final Object context = this.loadContext = new Object();

        FeedAggregator.getInstance().getItems(new FetchItemsCallback() {
            @Override
            public void onItemsLoaded(List<FeedItem> feedItems) {
                if (loadContext.equals(context)) {
                    addItems(feedItems);
                }
            }
        }, refresh);

    }
}
