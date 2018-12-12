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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuestionSet extends AppCompatActivity {
TextView questionValue;
   Button submitAnswer;
    static int questionCount=0;
    static SharedPreferences sharedQuestionCount;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button review;
    Button submitScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_set);


        questionValue =(TextView)findViewById(R.id.questions);
        submitAnswer =(Button)findViewById(R.id.submitAnswer);
        radioGroup =(RadioGroup) findViewById(R.id.radioGroup);
        review =(Button) findViewById(R.id.reviewAnswers);
        submitScore =(Button)findViewById(R.id.submitScore);
        submitScore.setVisibility(View.INVISIBLE);
        submitAnswer.setVisibility(View.VISIBLE);
        sharedQuestionCount = this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);

        questionValue.setText(SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0)));

        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton =  findViewById(radioId);

                    if(radioButton.getText().toString().equals("Poor"))
                    {
                        MainActivity.sharedTotalScore.edit()
                                .putInt("totalScore",MainActivity.sharedTotalScore.getInt("totalScore",0)+10)
                                 .apply();

                        MainActivity
                                .sharedEvaluvationReview
                                .edit()
                                .putString("evaluvationReview",
                                        MainActivity.sharedEvaluvationReview.getString("evaluvationReview","")+"\n"
                                        +SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0))+
                                        "\n You selected Poor")
                                        .apply();

                    }
                    if(radioButton.getText().toString().equals("Fair"))
                    {
                        MainActivity.sharedTotalScore.edit()
                                .putInt("totalScore",MainActivity.sharedTotalScore.getInt("totalScore",0)+20)
                                .apply();
                        MainActivity
                                .sharedEvaluvationReview
                                .edit()
                                .putString("evaluvationReview",
                                        MainActivity.sharedEvaluvationReview.getString("evaluvationReview","")+"\n"
                                                +SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0))+
                                                "\n You selected Fair")
                                .apply();

                    }
                    if(radioButton.getText().toString().equals("Good"))
                    {
                        MainActivity.sharedTotalScore.edit()
                                .putInt("totalScore",MainActivity.sharedTotalScore.getInt("totalScore",0)+30)
                                .apply();
                        MainActivity
                                .sharedEvaluvationReview
                                .edit()
                                .putString("evaluvationReview",
                                        MainActivity.sharedEvaluvationReview.getString("evaluvationReview","")+"\n"
                                                +SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0))+
                                                "\n You selected Good")
                                .apply();

                    }
                    if(radioButton.getText().toString().equals("Very Good"))
                    {
                        MainActivity.sharedTotalScore.edit()
                                .putInt("totalScore",MainActivity.sharedTotalScore.getInt("totalScore",0)+40)
                                .apply();
                        MainActivity
                                .sharedEvaluvationReview
                                .edit()
                                .putString("evaluvationReview",
                                        MainActivity.sharedEvaluvationReview.getString("evaluvationReview","")+"\n"
                                                +SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0))+
                                                "\n You selected Very Good")
                                .apply();

                    }
                    if(radioButton.getText().toString().equals("Superior"))
                    {
                        MainActivity.sharedTotalScore.edit()
                                .putInt("totalScore",MainActivity.sharedTotalScore.getInt("totalScore",0)+50)
                                .apply();
                        MainActivity
                                .sharedEvaluvationReview
                                .edit()
                                .putString("evaluvationReview",
                                        MainActivity.sharedEvaluvationReview.getString("evaluvationReview","")+"\n"
                                                +SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0))+
                                                "\n You selected Superior")
                                .apply();
                    }

                //    questionCount++;
                    int temp =sharedQuestionCount.getInt("questionCount",0);
                    ++temp;
                    sharedQuestionCount.edit().putInt("questionCount",temp).apply();

                    if(sharedQuestionCount.getInt("questionCount",0)<=SurveyOptions.questions.size()-1)
                    {

                        questionValue.setText(SurveyOptions.questions.get(sharedQuestionCount.getInt("questionCount",0)));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"PLEASE SUBMIT SURVEY NOW",Toast.LENGTH_SHORT).show();
                       // questionCount =0;
                        sharedQuestionCount.edit().putInt("questionCount",0).apply();
                        submitScore.setVisibility(View.VISIBLE);
                        submitAnswer.setVisibility(View.INVISIBLE);
                    }
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ReviewAnswers.class));

            }
        });

        submitScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTeamScore();
                Toast.makeText(getApplicationContext(),"SURVEY COMPLETED",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitTeamScore()
    {
        String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/score?"
                +"teamid="+SurveyOptions.sharedTeamId.getString("teamid","")+"&"
                +"score="+MainActivity.sharedTotalScore.getInt("totalScore",0)+"&"
                + "userId="+MainActivity.sharedUserId.getString("userid","");
//We need to add the jwt token in the authorization header
//Toast.makeText(getApplicationContext(), "String is: "+url,Toast.LENGTH_LONG).show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//SUCCESSFUL RESPONSE
                        try {
                                Toast.makeText(getApplicationContext(),"Team Score Updated Successfully!",Toast.LENGTH_SHORT).show();
                            new CountDownTimer(3000, 1000) {
                                public void onFinish() {
                                    // When timer is finished
                                    logoutTeam();
                                }

                                public void onTick(long millisUntilFinished) {
                                    // millisUntilFinished    The amount of time until finished.
                                }
                            }.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Team Score Updation not successful!",Toast.LENGTH_SHORT).show();
                            logoutTeam();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//UNSUCCESSFUL RESPONSE
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                logoutTeam();

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
    public void logoutTeam()
    {
        SurveyOptions.sharedLoginTeam.edit().putString("loginTeam","").apply();
        SurveyOptions.sharedTeamId.edit().putString("teamid","").apply();
        SurveyOptions.sharedTeamToken.edit().putString("teamToken","").apply();
        MainActivity.sharedEvaluvationReview.edit().putString("evaluvationReview","").apply();
        MainActivity.sharedTotalScore.edit().putInt("totalScore",0).apply();

        new CountDownTimer(1000, 1000) {
            public void onFinish() {
                // When timer is finished
                finish();
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();

    }
}




