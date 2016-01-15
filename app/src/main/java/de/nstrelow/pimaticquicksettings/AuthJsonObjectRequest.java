package de.nstrelow.pimaticquicksettings;

import android.util.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import java.util.*;
import org.json.*;

public class AuthJsonObjectRequest extends JsonObjectRequest
 {

	 private String user;
	 private String password;
	 
        public AuthJsonObjectRequest(int method, String url, String user, String password, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
			this.user = user;
			this.password = password;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<String, String>();
            params.put(
                    "Authorization",
                    String.format("Basic %s", Base64.encodeToString(
                            String.format("%s:%s", user, password).getBytes(), Base64.DEFAULT)));
            return params;
        }

    }
