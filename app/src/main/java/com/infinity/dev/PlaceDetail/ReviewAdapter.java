package com.infinity.dev.PlaceDetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.infinity.dev.Utility.LRUImageCache;
import com.infinity.dev.nearby.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by suny on 22/8/15.
 */
public class ReviewAdapter extends BaseAdapter {

    PlaceDetailBean.Review []reviews;
    Context context;
    LayoutInflater inflater;
    Typeface roboto_bold;

    String baseURL = "https://www.googleapis.com/plus/v1/people/";
    String key = "AIzaSyBg-iwzAjavEUVV9hOQUr0JljZHL7XFRkQ";

    String colors = "#005968";

    LRUImageCache imageCache = new LRUImageCache();

    public ReviewAdapter(PlaceDetailBean.Review []reviews, Context context){
        this.reviews = reviews;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        roboto_bold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Object getItem(int position) {
        return reviews[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.review_fragment_item, parent, false);
            RoundImageView icon = (RoundImageView)convertView.findViewById(R.id.author_icon);
            TextView author_name = (TextView)convertView.findViewById(R.id.author_name);
            TextView author_text = (TextView)convertView.findViewById(R.id.author_text);
            RatingBar author_rating = (RatingBar) convertView.findViewById(R.id.author_rating);

            author_name.setText(reviews[position].getAuthor_name());
            author_text.setText(reviews[position].getAuthor_text());
            author_rating.setRating(reviews[position].getRating());
            icon.setScaleType(ImageView.ScaleType.CENTER);
            GradientDrawable gd = (GradientDrawable) icon.getBackground().getCurrent();
            gd.setColor(Color.parseColor(colors));

            author_name.setTypeface(roboto_bold);
            author_text.setTypeface(roboto_bold);

            if(reviews[position].getAuthor_url() != null) {
                String author_url = baseURL + reviews[position].getAuthor_url().substring(24) + "?fields=image&key=" + key;
                new ImageLoader(author_url, icon, imageCache).loadThumbnailImage();
            }
        }else {
            RoundImageView icon = (RoundImageView)convertView.findViewById(R.id.author_icon);
            TextView author_name = (TextView)convertView.findViewById(R.id.author_name);
            TextView author_text = (TextView)convertView.findViewById(R.id.author_text);
            RatingBar author_rating = (RatingBar)convertView.findViewById(R.id.author_rating);

            author_name.setText(reviews[position].getAuthor_name());
            author_text.setText(reviews[position].getAuthor_text());
            author_rating.setRating(reviews[position].getRating());
            icon.setScaleType(ImageView.ScaleType.CENTER);
            GradientDrawable gd = (GradientDrawable) icon.getBackground().getCurrent();
            gd.setColor(Color.parseColor(colors));

            author_name.setTypeface(roboto_bold);
            author_text.setTypeface(roboto_bold);
            if(reviews[position].getAuthor_url() != null) {
                String author_url = baseURL + reviews[position].getAuthor_url().substring(24) + "?fields=image&key=" + key;
                new ImageLoader(author_url, icon, imageCache).loadThumbnailImage();
            }
        }
        return convertView;
    }
}
