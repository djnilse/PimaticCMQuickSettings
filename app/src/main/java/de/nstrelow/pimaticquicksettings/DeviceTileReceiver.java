package de.nstrelow.pimaticquicksettings;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import de.nstrelow.pimaticquicksettings.api.Device;

/**
 * Created by nilss on 18.01.2016.
 */
public class DeviceTileReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Device.ACTION_TOGGLE_DEVICE.equals(intent.getAction())) {
            Intent newIntent = new Intent();
            newIntent.setAction(Device.ACTION_TOGGLE_DEVICE);
            newIntent.putExtra(PimaticActivity.DEVICE_NAME, getDeviceName(intent));
            newIntent.putExtra(PimaticActivity.DEVICE_URL, getDeviceUrl(intent));

            String label = getDeviceName(intent) + getCurrentState(intent);

            int state = getCurrentState(intent);
            switch (state) {
                case Device.STATE_OFF:
                    newIntent.putExtra(PimaticActivity.DEVICE_STATE, Device.STATE_ON);
                    label = getDeviceName(intent) + " is " + Device.ON;
                    sendToDevice(context, getDeviceUrl(intent), Device.TURN_ON);
                    break;
                case Device.STATE_ON:
                    newIntent.putExtra(PimaticActivity.DEVICE_STATE, Device.STATE_OFF);
                    label = getDeviceName(intent) + " is " + Device.OFF;
                    sendToDevice(context, getDeviceUrl(intent), Device.TURN_OFF);
                    break;
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            CustomTile deviceTile = new CustomTile.Builder(context)
                    .setOnClickIntent(pendingIntent)
                    .setContentDescription("Pimatic Device Tile")
                    .setLabel(label)
                    .shouldCollapsePanel(false)
                    .setIcon(R.drawable.ic_menu_camera)
                    .build();

            CMStatusBarManager.getInstance(context)
                    .publishTile(PimaticActivity.DEVICE_TILE_ID, deviceTile);
        }
    }

    private void sendToDevice(Context context, String deviceUrl, String deviceState) {

        SingletonRequestQueue queue = SingletonRequestQueue.getInstance(context);

        String url = deviceUrl + deviceState;

        // Request a json response from the provided URL.
        AuthJsonObjectRequest authJsonObjectRequest = new AuthJsonObjectRequest(Request.Method.GET, url, PimaticActivity.userLocal, PimaticActivity.passwordLocal,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.addToRequestQueue(authJsonObjectRequest);
    }

    private int getCurrentState(Intent intent) {
        return intent.getIntExtra(PimaticActivity.DEVICE_STATE, 0);
    }

    private String getDeviceName(Intent intent) {
        return intent.getStringExtra(PimaticActivity.DEVICE_NAME);
    }

    private String getDeviceUrl(Intent intent) {
        return intent.getStringExtra(PimaticActivity.DEVICE_URL);
    }
}
