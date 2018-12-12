package com.example.gauth.amad_posterprojectevaluationappv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

Button login;
static Button logout;

    static  SharedPreferences sharedEvaluvatorToken;
    static  SharedPreferences sharedUserId;
    static SharedPreferences sharedLoginEvaluvator;
    static  SharedPreferences sharedEvaluvationReview;
    static SharedPreferences sharedTotalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       login =(Button)findViewById(R.id.loginEvaluvator);
       logout= (Button)findViewById(R.id.logout);

        sharedEvaluvatorToken =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedUserId =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedLoginEvaluvator =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedEvaluvationReview =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);
        sharedTotalScore =this.getSharedPreferences("com.example.gauth.amad_posterprojectevaluationappv2",Context.MODE_PRIVATE);

        if(sharedLoginEvaluvator.getString("loginEvaluvator","").equals("Login successful"))
{login.setText("BACK TO SURVEY");
    Toast.makeText(getApplicationContext(),"User logged in",Toast.LENGTH_SHORT).show();
logout.setVisibility(View.VISIBLE);
    startActivity(new Intent(getBaseContext(), SurveyOptions.class));

}
else{
            login.setText("LOGIN");
    Toast.makeText(getApplicationContext(),"User not logged in",Toast.LENGTH_SHORT).show();
            logout.setVisibility(View.INVISIBLE);
}

//LOGIN AS EVALUVATOR
 login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(sharedLoginEvaluvator.getString("loginEvaluvator","").equals("Login successful"))
               {
                   Toast.makeText(getApplicationContext(),"You are already logged in",Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(getBaseContext(), SurveyOptions.class));
               }else {
                   ScanCodeActivity.loginType = "EVALUVATOR";
                   startActivity(new Intent(getBaseContext(), ScanCodeActivity.class));
               }
               }
       });


    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            login.setText("LOGIN");
            //clear evaluvator data
            sharedLoginEvaluvator.edit().putString("loginEvaluvator","").apply();
            sharedUserId.edit().putString("userid","").apply();
            sharedEvaluvatorToken.edit().putString("evaluvatorToken","").apply();

            //clear question data
            sharedEvaluvationReview.edit().putString("evaluvationReview","").apply();;
            sharedTotalScore.edit().putInt("totalScore",0).apply();

            //clear team data
            SurveyOptions.sharedLoginTeam.edit().putString("loginTeam","").apply();
            SurveyOptions.sharedTeamToken.edit().putString("teamToken","").apply();
            SurveyOptions.sharedTeamId.edit().putString("teamid","").apply();
logout.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"LOGGED OUT SUCCESSFULLY",Toast.LENGTH_SHORT).show();
        }
    });
    }
}
