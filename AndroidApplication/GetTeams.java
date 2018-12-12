package com.example.gauth.amad_posterprojectevaluationappv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GetTeams extends AppCompatActivity {
    ListView listView;

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> teamValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_teams);

        listView =(ListView) findViewById(R.id.teamList);
        teamValues =new ArrayList<>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,teamValues);
        listView.setAdapter(arrayAdapter);

        getTeamDetails();
    }

    public void getTeamDetails()
    {
        AllTeamScores.teamScores.clear();

       // String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/teams";
        String url ="http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/teams";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//SUCCESSFUL RESPONSE
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                       JSONObject teams = jsonArray.getJSONObject(i);
                      // Toast.makeText(getApplicationContext(),teams.getJSONObject("score").getString("$numberDecimal"),Toast.LENGTH_SHORT).show();
                                         AllTeamScores
                                        .teamScores
                                        .add(new AllTeamScores(teams.getString("teamId"),
                                                Float.valueOf(teams.getString("evaluationscount")),
                                                Float.valueOf(teams.getJSONObject("score").getString("$numberDecimal"))));
                          //      Toast.makeText(getApplicationContext(),teams.getString("teamId"),Toast.LENGTH_SHORT).show();
                            }
                            //Sort the collection
                            Collections.sort(AllTeamScores.teamScores, new Comparator<AllTeamScores>() {
                                @Override
                                public int compare(AllTeamScores t1, AllTeamScores t2) {
                                    return (int) (-(t1.score - t2.score));  //SORT BY AVERAGE SCORE descending

                                }
                            });
                            for(int i=0; i<AllTeamScores.teamScores.size();i++)
                            {
                                teamValues.add("TEAM NAME "+AllTeamScores.teamScores.get(i).teamName
                                        +"\n"+"EVALUVATION COUNT "+AllTeamScores.teamScores.get(i).evaluvationCount
                                +"\n"+"Scores "+AllTeamScores.teamScores.get(i).score);
                            }
                            arrayAdapter.notifyDataSetChanged();

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
            public Map getHeaders() throws AuthFailureError { ;
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


