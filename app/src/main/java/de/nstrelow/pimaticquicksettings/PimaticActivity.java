package de.nstrelow.pimaticquicksettings;

import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import cyanogenmod.app.*;
import org.json.*;

import android.support.v7.widget.Toolbar;

public class PimaticActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int TEMP_TILE_ID = 25432;
    private TextView mTextView;

    private static final String TAG_VARIABLE = "variable";
    private static final String TAG_VALUE = "value";
	private static final String TAG_UNIT="unit";

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

    public void addQuickSetting(final View view) {
        // Instantiate the RequestQueue.
        SingletonRequestQueue queue = SingletonRequestQueue.getInstance(view.getContext());
		
        String serverIP = "djnilse.ddns.net";
		String variablesUrl = "http://" + serverIP + "/api/variables/";
		String deviceVariable = "temp.temperature";
		String user = "app";
		String password = "ohhwhatstronkpassworT";

		String url = variablesUrl + deviceVariable;
		
        // Request a json response from the provided URL.
        AuthJsonObjectRequest authJsonObjectRequest = new AuthJsonObjectRequest(Request.Method.GET, url + deviceVariable, user, password,
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
