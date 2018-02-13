package com.spectraapps.myspare;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.spectraapps.myspare.login.LoginActivity;
import com.spectraapps.myspare.model.LoginModel;

public class SplashScreen extends Activity {

    ImageButton button_ar;
    ImageButton button_en;
    public static int LANG_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        button_ar = findViewById(R.id.btn_ar);
        button_en = findViewById(R.id.btn_en);

        insertAnimation();
        initButtonClickListener();
    }//end oncreate

    private void initButtonClickListener() {
        button_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANG_NUM = 1;
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        button_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANG_NUM = 2;

                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void insertAnimation() {

        Animation animation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.splash_anim);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(800);

        ImageView splash = findViewById(R.id.splach_screen);
        splash.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                YoYo.with(Techniques.SlideInUp).playOn(button_ar);
                YoYo.with(Techniques.SlideInDown).playOn(button_en);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }//end insert anim
}
