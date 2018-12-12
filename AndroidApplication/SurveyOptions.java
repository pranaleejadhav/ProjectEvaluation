package com.example.gauth.amad_posterprojectevaluationappv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SurveyOptions extends AppCompatActivity {
Button getAllTeamScores;
Button startSurvey;
Button reviewAnswers2;
Button selectTeam;
    static SharedPreferences sharedTeamToken;
    static SharedPreferences sharedTeamId;
    static SharedPreferences sharedLoginTeam;

    public  static ArrayList<String> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_options);
        getAllTeamScores= (Button) findViewById(R.id.getAllTeamScores);
        startSurvey =(Button)findViewById(R.id.startSurvey);
        reviewAnswers2 =(Button)findViewById(R.id.reviewAnswers2);
        selectTeam =(Button)findViewById(R.id.selectTeam);

        sharedTeamToken =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedTeamId =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedLoginTeam =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
MainActivity.logout.setVisibility(View.VISIBLE);

selectTeam.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if((SurveyOptions.sharedLoginTeam.getString("loginTeam","").equals("Login successful")))
        {
            Toast.makeText(getApplicationContext(),"PLEASE COMPLETE THE SURVEY",Toast.LENGTH_SHORT).show();
        }
        else{
            ScanCodeActivity.loginType ="TEAM";
            startActivity(new Intent(getBaseContext(), ScanCodeActivity.class));
            MainActivity.sharedEvaluvationReview.edit().putString("evaluvationReview","").apply();
            MainActivity.sharedTotalScore.edit().putInt("totalScore",0).apply();
        }
    }
});
        getAllTeamScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getEvaluvatorJWTToken();  //this line will not be needed once we get tokens from qrcode
                Toast.makeText(getApplicationContext(),
                        "JWT IS "+MainActivity.sharedEvaluvatorToken.getString("evaluvatorToken",""),
                        Toast.LENGTH_LONG).show();
                 startActivity(new Intent(getBaseContext(), GetTeams.class));
            }
        });

        startSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(SurveyOptions.sharedLoginTeam.getString("loginTeam","").equals("Login successful"))
               {
                   downloadQuestions();

                   new CountDownTimer(4000, 1000) {
                       public void onFinish() {
                           // When timer is finished
                           startActivity(new Intent(getBaseContext(), QuestionSet.class));
                           Toast.makeText(getApplicationContext(),"SURVEY STARTED",Toast.LENGTH_SHORT).show();
                       }
                       public void onTick(long millisUntilFinished) {
                           // millisUntilFinished    The amount of time until finished.
                       }
                   }.start();
               }
               else{
                   Toast.makeText(getApplicationContext(),"PLEASE SELECT A TEAM",Toast.LENGTH_SHORT).show();
               }
                }
        });

        reviewAnswers2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(getBaseContext(), ReviewAnswers.class));
            }
        });
    }

    public void downloadQuestions()
    {
        questions.clear();

        String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/questions";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//SUCCESSFUL RESPONSE
                        try {
                            int count =0;
                            JSONArray jsonArray = response.getJSONArray("data");  //questions changed to data
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject individualQuestion = jsonArray.getJSONObject(i);

                                questions.add(individualQuestion.getString("question"));

                            if(count==jsonArray.length()-1)
                            {
                                //startActivity(new Intent(getBaseContext(), DownloadedQuestionSet.class));
                            Toast.makeText(getApplicationContext(),"Questions Downloaded Successfully!",Toast.LENGTH_SHORT).show();
                            }
                            count++;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Parse error "+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//UNSUCCESSFUL RESPONSE
                Toast.makeText(getApplicationContext(), "Response error "+error.toString(),Toast.LENGTH_SHORT).show();
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
}

