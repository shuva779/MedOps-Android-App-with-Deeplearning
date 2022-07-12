package com.istiaksaif.medops.Utils;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.istiaksaif.medops.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotifySender {
    String userFcmToken,title,body;
    Context mContext;
    Activity mActivity;
    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA19dyi8w:APA91bH-pSLyGfgbXcT0H4i8mZbOy-XUo7_R3ivw0fNEnv-xLnY6vuoZZhUHc5SRQeikTuHArY0H6o1rJj8tB7jHZE02EwQwzSCjEvAKlM-4UfsTEVNsXH-z0ALnqSbVsqN0TtFfg4w1";

    public FcmNotifySender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotify(){
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to",userFcmToken);
            JSONObject notifyJsonObject = new JSONObject();
            notifyJsonObject.put("title",body);
            notifyJsonObject.put("icon", "icon");

            jsonObject.put("notification",notifyJsonObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String,String> map = new HashMap<>();
                    map.put("Authorization","Key="+fcmServerKey);
                    map.put("ContentType","application/json");
                    return map;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            request.setRetryPolicy(new DefaultRetryPolicy(3000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);

        }catch (Exception e){

        }
    }
}
