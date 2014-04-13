package net.imthinker.android.httpstacks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.imthinker.android.httpstacks.libs.OkHttpStack;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    private RequestQueue sRequestQueue;
    private RequestQueue sDefaultHttpClientRequestQueue;
    private RequestQueue sOkHttpRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // デフォルトクッキーマネージャ
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // リクエストキューをセット
        sRequestQueue = Volley.newRequestQueue(this);

        // DefaultHttpClient を使ったリクエストキューをセット
        DefaultHttpClient httpClient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        httpClient.setCookieStore(cookieStore);
        HttpStack httpStack = new HttpClientStack(httpClient);
        sDefaultHttpClientRequestQueue = Volley.newRequestQueue(this, httpStack);

        // OkHttp を使ったリクエストキューをセット
        sOkHttpRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());
    }

    private JsonObjectRequest createRequest() {
        return new JsonObjectRequest(
                Request.Method.GET,
                "http://httpbin.org/headers",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        VolleyLog.d(jsonObject.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        VolleyLog.e(volleyError.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Volley Sample Application 1.0.0");
                return params;
            }
        };
    }

    public void normalVolley(View v) {
        Toast.makeText(this, "普通の Volley スタック", Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = this.createRequest();
        jsonObjectRequest.setTag(1);
        sRequestQueue.add(jsonObjectRequest);
    }

    public void stackVolley(View v) {
        Toast.makeText(this, "DefaultHttpClient を使った Volley スタック", Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = this.createRequest();
        jsonObjectRequest.setTag(2);
        sDefaultHttpClientRequestQueue.add(jsonObjectRequest);
    }

    public void okhttpVolley(View v) {
        Toast.makeText(this, "OkHttp を使った Volley スタック", Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = this.createRequest();
        jsonObjectRequest.setTag(3);
        sOkHttpRequestQueue.add(jsonObjectRequest);
    }
}
