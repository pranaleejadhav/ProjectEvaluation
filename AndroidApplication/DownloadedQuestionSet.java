package com.example.gauth.amad_posterprojectevaluationappv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DownloadedQuestionSet extends AppCompatActivity {

    ListView downloadedQuestions;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_question_set);

        downloadedQuestions =(ListView) findViewById(R.id.downloadedQuestions);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,SurveyOptions.questions);
        downloadedQuestions.setAdapter(arrayAdapter);

    }

}
