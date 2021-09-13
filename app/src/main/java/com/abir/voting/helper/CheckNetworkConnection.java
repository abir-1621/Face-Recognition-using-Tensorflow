package com.abir.voting.helper;

import android.content.Context;
import android.os.AsyncTask;

public class CheckNetworkConnection extends AsyncTask< Void, Void, Boolean > {
    private final OnConnectionCallback onConnectionCallback;
    private final Context context;

    public CheckNetworkConnection(Context con, OnConnectionCallback onConnectionCallback) {
        super();
        this.onConnectionCallback = onConnectionCallback;
        this.context = con;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void...params) {
        if (context == null)
            return false;

        boolean isConnected = new NetWorkInfoUtility().isNetWorkAvailableNow(context);
        return isConnected;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        super.onPostExecute(b);

        if (b) {
            onConnectionCallback.onConnectionSuccess();
        } else {
            String msg = "No Internet Connection";
            if (context == null)
                msg = "Context is null";
            onConnectionCallback.onConnectionFail(msg);
        }

    }

    public interface OnConnectionCallback {
        void onConnectionSuccess();

        void onConnectionFail(String errorMsg);
    }
    
}
