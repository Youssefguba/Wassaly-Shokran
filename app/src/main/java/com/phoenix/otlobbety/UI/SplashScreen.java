package com.phoenix.otlobbety.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.phoenix.otlobbety.R;

public class SplashScreen extends Activity {
    Thread splashTread;
    ImageView imageView;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();

        window.setFormat(PixelFormat.RGBA_8888);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startAnimation();

    }


    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.reset();

        RelativeLayout relativeLayout = findViewById(R.id.relativeLay);
        relativeLayout.clearAnimation();
        relativeLayout.setAnimation(animation);


        animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        animation.reset();
        imageView = findViewById(R.id.splashImg);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.clearAnimation();
        imageView.setAnimation(animation);

        splashTread = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                try {

                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                }

            }
        };
        splashTread.start();
    }
}

