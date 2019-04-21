package com.treefunk.geoquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_INDEX = "index";
    private static final String KEY_HAS_CHEATED = "cheated";
    private static final String KEY_CHEAT_COUNT = "cheat_count";

    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mCheatsTokenTextView;
    private Boolean answerShown = false;

    private int mCheatsCount = 3;

    private Question[] mQuestions = new Question[]{
            new Question(R.string.question_africa,false),
            new Question(R.string.question_americas,true),
            new Question(R.string.question_asia,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_australia,true)
    };

    private int[] answered = new int[]{
            0,0,0,0,0,0
    };

    private int score = 0;
    private int answeredQuestions = 0;

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            answerShown = savedInstanceState.getBoolean(KEY_HAS_CHEATED,false);
            mCheatsCount = savedInstanceState.getInt(KEY_CHEAT_COUNT,3);
        }

        if(answered[mCurrentIndex] == 1){
            disableTrueFalseButtons(true);
        }

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mQuestionTextView = findViewById(R.id.question_text_view);
        mPreviousButton = findViewById(R.id.previous_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatsTokenTextView = findViewById(R.id.cheats_token_text_view);

        checkCheatIfValid();

        mCheatsTokenTextView.setText("Cheats token: " + Integer.toString(mCheatsCount));
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(MainActivity.this,mQuestions[mCurrentIndex].isAnswerTrue());
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();

        mNextButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                updateQuestion();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mCurrentIndex > 0){
                    mCurrentIndex = (mCurrentIndex - 1);
                }
                updateQuestion();
            }
        });


        mTrueButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(true);
                    }
                }
        );

        mFalseButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        checkAnswer(false);
                    }
                }
        );
    }

    private void updateQuestion(){
        int question = mQuestions[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if(answered[mCurrentIndex] == 1){
            disableTrueFalseButtons(true);
        }else{
            disableTrueFalseButtons(false);
        }


    }

    private void checkAnswer(boolean userPressedTrue){

        boolean answerTrue = mQuestions[mCurrentIndex].isAnswerTrue();
        Toast toast;
        int toastMsg;

        if(userPressedTrue == answerTrue){
            score++;
            toastMsg = R.string.correct_toast;
        }else{
            toastMsg = R.string.incorrect_toast;
        }
        answeredQuestions++;
        toast = Toast.makeText(MainActivity.this,
                toastMsg,
                Toast.LENGTH_SHORT);

        answered[mCurrentIndex] = 1;

        toast.setGravity(Gravity.TOP,Gravity.CENTER_HORIZONTAL,50);
        toast.show();
        disableTrueFalseButtons(true);

        if(answeredQuestions == mQuestions.length){
            Toast.makeText(this,"Score:" + Integer.toString(score),Toast.LENGTH_LONG).show();
        }
    }

    private void disableTrueFalseButtons(boolean disable){
        mTrueButton.setClickable(!disable);
        mFalseButton.setClickable(!disable);
    }

    private void checkIfAnswered(int index){
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        savedInstanceState.putBoolean(KEY_HAS_CHEATED,answerShown);
        savedInstanceState.putInt(KEY_CHEAT_COUNT,mCheatsCount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }

            if(resultCode == RESULT_OK ){
                answerShown = CheatActivity.wasAnswerShown(data);
            }

            if(resultCode == CheatActivity.RESULT_CHEAT_USED){
                boolean cheatUsed = CheatActivity.cheatUsed(data);
                Log.i("CHEAT_USED",Boolean.toString(cheatUsed));
                if(cheatUsed){
                    mCheatsCount--;
                    mCheatsTokenTextView.setText("Cheats token: " + mCheatsCount);
                    checkCheatIfValid();
                }
            }
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(answerShown){
            Toast.makeText(this,R.string.judgment_toast,Toast.LENGTH_LONG).show();
        }
    }

    private void checkCheatIfValid()
    {
        if(mCheatsCount == 0){
            mCheatButton.setVisibility(View.INVISIBLE);
        }
    }


}
