package com.infinity.dev.Utility;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by suny on 12/9/15.
 */
public class LRUImageCache {

    private HashMap<String, Bitmap> imageCache;

    public LRUImageCache(){
        imageCache = new HashMap<>();
    }

    public boolean addBitmaptoCache(String k, Bitmap v){
        try{
            imageCache.put(k, v);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public Bitmap getBitmapFromCache(String k){
        if(imageCache.containsKey(k)){
            return imageCache.get(k);
        }else {
            return null;
        }
    }
}
