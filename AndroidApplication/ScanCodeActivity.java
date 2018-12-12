package com.example.gauth.amad_posterprojectevaluationappv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;
    static String loginType ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    ScannerView = new ZXingScannerView(this);
    setContentView(ScannerView);

        //    setContentView(R.layout.activity_scan_code);
    }

    @Override
    public void handleResult(final Result result) {
     if(loginType.equals("EVALUVATOR"))
     {

        MainActivity.sharedEvaluvatorToken.edit().putString("evaluvatorToken",result.getText()).apply();
        Log.i("TOKEN IS! ",MainActivity.sharedEvaluvatorToken.getString("evaluvatorToken",""));
         //ABOVE STATEMENT TO BE UNCOMMENTED

         new CountDownTimer(1000, 1000) {
             public void onFinish() {
                 // When timer is finished
                 loginEvaluvator();   //Check the evaluvator credentials  TO BE UNCOMMENTED
             }

             public void onTick(long millisUntilFinished) {
                 // millisUntilFinished    The amount of time until finished.
             }
         }.start();

     }
     if(loginType.equals("TEAM"))
     {
         JWTTokens.teamToken = result.getText();
         SurveyOptions.sharedTeamToken.edit().putString("teamToken",result.getText()).apply();
         selectTeam();
     }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(),"Camera stopped",Toast.LENGTH_SHORT).show();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }

    public  void loginEvaluvator()
    {
        String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/login";
//We need to add the jwt token in the authorization header

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
//SUCCESSFUL RESPONSE
  //Check if jwt token is correct
                        try {
                            Toast.makeText(getApplicationContext(),"Response is "+response.toString(),Toast.LENGTH_SHORT).show();
                            MainActivity.sharedLoginEvaluvator.edit().putString("loginEvaluvator",response.getString("message")).apply();

                            new CountDownTimer(1000, 1000) {
                                public void onFinish() {
                                    // When timer is finished
                                    if(MainActivity.sharedLoginEvaluvator.getString("loginEvaluvator","").equals("Login successful"))
                                    {

//                                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();

                                        try {
                                            JWTTokens.userid = response.getString("userid");
                                            MainActivity.sharedUserId.edit().putString("userid",response.getString("userid")).apply();
                                        }
                                         catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        startActivity(new Intent(getBaseContext(), SurveyOptions.class));
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Unsuccessful log in.",Toast.LENGTH_SHORT).show();
                                        MainActivity.sharedEvaluvatorToken.edit().putString("evaluvatorToken","").apply();
                                        MainActivity.sharedLoginEvaluvator.edit().putString("loginEvaluvator","").apply();
                                        MainActivity.sharedUserId.edit().putString("userid","").apply();
                                        onBackPressed();
                                    }
                                }

                                public void onTick(long millisUntilFinished) {
                                    // millisUntilFinished    The amount of time until finished.
                                }
                            }.start();

                        }
                        catch (JSONException e) {
                            MainActivity.sharedEvaluvatorToken.edit().putString("evaluvatorToken","").apply();
                            MainActivity.sharedLoginEvaluvator.edit().putString("loginEvaluvator","").apply();
                            MainActivity.sharedUserId.edit().putString("userid","").apply();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//UNSUCCESSFUL RESPONSE
                /*Toast.makeText(getApplicationContext(),
                        "JWT IS "+MainActivity.sharedEvaluvationReview.getString("evaluvatorToken","")+" HELLO ",
                         Toast.LENGTH_LONG).show();*/
                Toast.makeText(getApplicationContext(),"INVALID TOKEN",Toast.LENGTH_SHORT).show();
                MainActivity.sharedEvaluvatorToken.edit().putString("evaluvatorToken","").apply();
                MainActivity.sharedLoginEvaluvator.edit().putString("loginEvaluvator","").apply();
                MainActivity.sharedUserId.edit().putString("userid","").apply();
                onBackPressed();
            }
        })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization",
                        "Bearer"+" "+MainActivity.sharedEvaluvatorToken.getString("evaluvatorToken",""));
                return headers;
            }
        };
        HandleApiRequests.getInstance(this).addToRequestQueue(request);
    }


    public void selectTeam()
    {

        String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/loginteam";
//We need to add the jwt token in the authorization header

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//SUCCESSFUL RESPONSE
                        try {
                            SurveyOptions.sharedLoginTeam.edit().putString("loginTeam",response.getString("message")).apply();
                            if(SurveyOptions.sharedLoginTeam.getString("loginTeam","").equals("Login successful"))
                            {

                                JWTTokens.teamid = response.getString("teamid");

                                SurveyOptions.sharedTeamId.edit().putString("teamid",response.getString("teamid")).apply();

                                Toast.makeText(getApplicationContext(),JWTTokens.teamid+" selected",Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Team not selected",Toast.LENGTH_SHORT).show();
                                SurveyOptions.sharedLoginTeam.edit().putString("loginTeam","").apply();
                                SurveyOptions.sharedTeamToken.edit().putString("teamToken","");
                                SurveyOptions.sharedTeamId.edit().putString("teamid","");

                                onBackPressed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            SurveyOptions.sharedLoginTeam.edit().putString("loginTeam","").apply();
                            SurveyOptions.sharedTeamToken.edit().putString("teamToken","");
                            SurveyOptions.sharedTeamId.edit().putString("teamid","");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//UNSUCCESSFUL RESPONSE
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                SurveyOptions.sharedLoginTeam.edit().putString("loginTeam","").apply();
                SurveyOptions.sharedTeamToken.edit().putString("teamToken","");
                SurveyOptions.sharedTeamId.edit().putString("teamid","");

                onBackPressed();
            }
        })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization",
                        "Bearer"+" "+SurveyOptions.sharedTeamToken.getString("teamToken",""));
                return headers;
            }
        };
        HandleApiRequests.getInstance(this).addToRequestQueue(request);

    }
}
