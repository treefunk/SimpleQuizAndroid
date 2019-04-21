package com.treefunk.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.treefunk.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.treefunk.geoquiz.answer_shown";
    private static final String CHEAT_USED = "com.treefunk.geoquiz.cheat_used";
    protected static final int RESULT_CHEAT_USED = 3;

    private boolean mAnswerIsTrue;
    private Button mShowButton;
    private TextView mAnswerTextView;
    private TextView mVersionTextView;

    public static Intent newIntent(Context packageContent, boolean answerIsTrue){
        Intent intent = new Intent(packageContent,CheatActivity.class)
                .putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);



        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);

        mShowButton = findViewById(R.id.show_answer_button);
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mVersionTextView = findViewById(R.id.api_version_text_view);
        mVersionTextView.setText(Integer.toString(Build.VERSION.SDK_INT));
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
                setCheatUsed();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowButton.getWidth() / 2;
                    int cy = mShowButton.getHeight() / 2;
                    float radius = mShowButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                }else{
                    mShowButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }

    public static boolean cheatUsed(Intent intent){
        return intent.getBooleanExtra(CHEAT_USED,false);
    }

    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        setResult(RESULT_OK,data);
    }

    private void setCheatUsed(){
        Intent data = new Intent();
        data.putExtra(CHEAT_USED,true);
        setResult(RESULT_CHEAT_USED,data);
    }
}
