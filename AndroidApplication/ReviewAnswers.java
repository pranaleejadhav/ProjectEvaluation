package com.example.gauth.amad_posterprojectevaluationappv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewAnswers extends AppCompatActivity {

    Button goBack;
    static TextView reviewAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
    goBack =(Button) findViewById(R.id.goBack);
    reviewAnswers =(TextView) findViewById(R.id.reviewAnswers);

    reviewAnswers.setText(MainActivity.sharedEvaluvationReview.getString("evaluvationReview",""));

    if(reviewAnswers.getText().toString().length() ==0)
    {
        Toast.makeText(getApplicationContext(),"NO QUESTIONS HAVE BEEN ANSWERED YET",Toast.LENGTH_SHORT).show();
    }
    goBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();

        }
    });
    }
}
