package de.nstrelow.pimaticquicksettings;

import android.content.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;

/**
 * Created by nilss on 12.01.2016.
 */
public class SingletonRequestQueue {
        private static SingletonRequestQueue mInstance;
        private RequestQueue mRequestQueue;
        private static Context mCtx;

        private SingletonRequestQueue(Context context) {
            mCtx = context;
            mRequestQueue = getRequestQueue();
        }

        public static synchronized SingletonRequestQueue getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new SingletonRequestQueue(context);
            }
            return mInstance;
        }

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

}
