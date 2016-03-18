package com.infinity.dev.PlaceDetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.infinity.dev.Utility.LRUImageCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by suny on 22/8/15.
 */
public class ImageLoader {

    String url;
    ImageView imageView;
    LRUImageCache imageCache;

    public ImageLoader(String url, ImageView imageView, LRUImageCache imageCache){
        this.url = url;
        this.imageView = imageView;
        this.imageCache = imageCache;
    }

    public void loadImage(){
        new DownloadImageTask().execute(url);
    }

    public void loadThumbnailImage(){
        new DownloadThumbImageTask().execute(url);
    }

    private InputStream OpenHTTPConnection(String url) throws IOException
    {

        InputStream in = null;
        int response;
        URL in_url = new URL(url);
        URLConnection connection = in_url.openConnection();

        try
        {
            HttpURLConnection httpcon = (HttpURLConnection)connection;
            httpcon.setRequestMethod("GET");
            httpcon.connect();
            response = httpcon.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpcon.getInputStream();
            }
        }
        catch(Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Networking Error!");
        }

        return in;
    }

    private Bitmap DownloadImage(String URL)
    {
        InputStream in;

        Bitmap cachedImage = imageCache.getBitmapFromCache(url);
        try
        {
            if(cachedImage == null) {
                Log.e(url, "Not found");
                in = OpenHTTPConnection(URL);
                cachedImage = BitmapFactory.decodeStream(in);
                imageCache.addBitmaptoCache(url, cachedImage);
                in.close();
            }
            else{
                Log.d(url, "Found");
                return cachedImage;
            }
        }
        catch(IOException ex)
        {
            Log.e("ImageLoader", ex.getLocalizedMessage());
        }

        return cachedImage;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String...urls)
        {
            Log.d("doInBackground()", "Downloading Image");
            return DownloadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result)
        {
            ImageView img = imageView;
            Log.d("doPostExecute()", "Setting Image");
            img.setImageBitmap(result);
        }
    }

    private class DownloadThumbImageTask extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String...urls)
        {
            Log.d("doInBackground()", "Downloading Image");
            return DownloadImage(getThumbURL(fetchNow(urls[0])));
        }

        protected void onPostExecute(Bitmap result)
        {
            ImageView img = imageView;
            Log.d("doPostExecute()", "Setting Image");
            img.setImageBitmap(result);
        }

        public String fetchNow(String url){
            HttpURLConnection connection = null;
            BufferedReader buffer = null;
            StringBuffer stringBuffer = new StringBuffer();

            try{
                URL cloudURL = new URL(url);

                connection = (HttpURLConnection)cloudURL.openConnection();
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                buffer = new BufferedReader(new InputStreamReader(stream));
                String line;

                while((line = buffer.readLine()) != null){
                    stringBuffer.append(line + "\n");
                }

            }catch (SocketTimeoutException ex){
                Log.e("ImageLoader", "Socket connection timeout", ex);
            }catch (Exception ex){
                Log.e("ERROR", "Unknown Error", ex);
            }
            finally {
                if(connection != null){
                    connection.disconnect();
                }
                if(buffer != null){
                    try{
                        buffer.close();
                    }catch (IOException ex){
                        Log.e("ImageLoader", "Error Closing Stream", ex);
                    }
                }
            }
            return stringBuffer.toString();
        }

        public String getThumbURL(String data){
            String result = null;
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(jsonObject.has("image")){
                    String []temp = jsonObject.getJSONObject("image").getString("url").split("=");
                    result = temp[0] + "=100";
                }
            }catch (JSONException ex){
                Log.e("Review Adapter","Unable to load author icon");
            }
            return result;
        }
    }
}
