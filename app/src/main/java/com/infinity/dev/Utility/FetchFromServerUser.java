package com.infinity.dev.Utility;

public interface FetchFromServerUser {
    void onPreFetch();
    void onFetchCompletion(String string, int id);
}