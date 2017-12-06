package hu.ait.android.dungeoncrawler.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import hu.ait.android.dungeoncrawler.R;

public class StartScreenActivity extends AppCompatActivity {

    private Animation rightLeftAnim;
    private Animation leftRightAnim;
    private TextView tvDungeon;
    private TextView tvCrawler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        tvDungeon = (TextView) findViewById(R.id.tvDungeon);
        tvCrawler = (TextView) findViewById(R.id.tvCrawler);
        initAnimations();
        initTimer();
    }

    private void initTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(StartScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 3500);
    }

    private void initAnimations() {
        rightLeftAnim = AnimationUtils.loadAnimation(StartScreenActivity.this,
                R.anim.right_left_anim);
        leftRightAnim = AnimationUtils.loadAnimation(StartScreenActivity.this,
                R.anim.left_right_anim);
        tvDungeon.setAnimation(rightLeftAnim);
        tvCrawler.setAnimation(leftRightAnim);
    }
}
