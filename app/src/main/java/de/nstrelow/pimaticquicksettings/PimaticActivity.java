package de.nstrelow.pimaticquicksettings;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

import de.nstrelow.pimaticquicksettings.api.Device;

public class PimaticActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int TEMP_TILE_ID = 25432;
    public static final int DEVICE_TILE_ID = 8451752;
    private TextView mTextView;

    private static final String TAG_VARIABLE = "variable";
    private static final String TAG_VALUE = "value";
    private static final String TAG_UNIT = "unit";


    public static String serverIP = "192.168.1.10";
    public static String variables = "/api/variables/";
    public static String device = "/api/device/";
    public static String variablesUrl = "http://" + serverIP + variables;
    public static String deviceUrl = "http://" + serverIP + device;
    public static String deviceVariable = "temp.temperature";
    public static String user = "app";
    public static String password = "ohhwhatstronkpassworT";
    public static String userLocal = "nilse";
    public static String passwordLocal = "sh@k1ra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pimatic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTextView = (TextView) findViewById(R.id.textView);
    }

    private String getTextById(@IdRes int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    public void getPimaticConfig() {
        if(!getTextById(R.id.editTextServerIP).isEmpty())
            serverIP = getTextById(R.id.editTextServerIP);
        if(!getTextById(R.id.editTextUser).isEmpty())
            user = getTextById(R.id.editTextUser);
        if(!getTextById(R.id.editTextPassword).isEmpty())
            password = getTextById(R.id.editTextPassword);
        deviceVariable = getTextById(R.id.editTextDevice);

        variablesUrl = "http://" + serverIP + variables;
        deviceUrl = "http://" + serverIP + device;
    }

    public static final String DEVICE_STATE = "device_state";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_URL = "device_url";


    public void addActionTile(final View view) throws IOException {

        SingletonRequestQueue queue = SingletonRequestQueue.getInstance(view.getContext());

        getPimaticConfig();

        String url = deviceUrl + deviceVariable;

        Intent intent = new Intent();
        intent.setAction(Device.ACTION_TOGGLE_DEVICE);
        intent.putExtra(PimaticActivity.DEVICE_STATE, Device.STATE_OFF);
        intent.putExtra(PimaticActivity.DEVICE_NAME, deviceVariable);
        intent.putExtra(PimaticActivity.DEVICE_URL, url);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Instantiate a builder object
        CustomTile deviceTile = new CustomTile.Builder(this)
                .setOnClickIntent(pendingIntent)
                .setContentDescription("Pimatic Device Tile")
                .setLabel(deviceVariable + " is " + Device.OFF)
                .shouldCollapsePanel(false)
                .setIcon(R.drawable.ic_menu_camera)
                .build();


        CMStatusBarManager.getInstance(this)
                .publishTile(DEVICE_TILE_ID, deviceTile);

    }

    private void httpPost(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try

        {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            OutputStreamWriter wout = new OutputStreamWriter(out);
            writeAction(wout);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //readStream(in);
        } finally

        {
            urlConnection.disconnect();
        }
    }

    void writeAction(OutputStreamWriter writer) {
        try {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addInfoTile(final View view) {

        SingletonRequestQueue queue = SingletonRequestQueue.getInstance(view.getContext());

        getPimaticConfig();

        String url = variablesUrl + deviceVariable;

        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password.toCharArray());
            }
        });

        // Request a json response from the provided URL.
        JsonObjectRequest authJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String temp = response.getJSONObject(TAG_VARIABLE).getDouble(TAG_VALUE) + "°C";
                            Snackbar.make(view, "Updating Temperature to " + temp, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            CustomTile temperatureTile = new CustomTile.Builder(getApplicationContext())
                                    .setContentDescription("Pimatic Room Temperature")
                                    .setLabel(temp)
                                    .shouldCollapsePanel(false)
                                    .setIcon(R.mipmap.ic_temperature)
                                    .build();

                            CMStatusBarManager.getInstance(getApplicationContext())
                                    .publishTile(TEMP_TILE_ID, temperatureTile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.addToRequestQueue(authJsonObjectRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pimatic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
