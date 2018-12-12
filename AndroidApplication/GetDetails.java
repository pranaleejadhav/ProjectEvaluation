package com.example.gauth.amad_posterprojectevaluationappv2;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class GetDetails
{

}

class JWTTokens
{
   public static String evaluvatorToken="";
   public static  String teamToken ="";
   public static String userid="";
   public static String teamid="";

}

class AllTeamScores
{

String teamName="";
float evaluvationCount =0;
float score =0;
static ArrayList<AllTeamScores> teamScores = new ArrayList<>();

public  AllTeamScores(String teamName,float evaluvationCount,float score)
{
   this.teamName  = teamName;
   this.evaluvationCount =evaluvationCount;
   this.score =score;

}

}

class AllQuestions
{
  // public  static ArrayList<String> questions = new ArrayList<>();
   static boolean isQuestionDownloaded =false;
   public static void addQuestions()
   {
      /*
      AllQuestions.questions.add("How long have you used our products?");
      AllQuestions.questions.add("Which of our products/services do you use?");
      AllQuestions.questions.add("How frequently do you purchase from us?");
      AllQuestions.questions.add("How did you like our project?");
      AllQuestions.questions.add("What is the chance you will come back?");*/
   }
}


