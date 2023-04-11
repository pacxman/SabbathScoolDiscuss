package com.ssdiscusskiny.utils;

import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.LinearInterpolator;

import com.bhprojects.bibleprojectkiny.SimpleFormatter;
import com.ssdiscusskiny.app.Variables;

public class AnimationAutoTextScroller {

    private Animation animator;
    private TextView scrollingTextView;
    private int duration = 30000;  // default value
    private SimpleFormatter simpleText;

    public AnimationAutoTextScroller(TextView tv, float screenwidth, SimpleFormatter simpleText) {

        this.scrollingTextView = tv;
        this.animator = new TranslateAnimation(
                Animation.ABSOLUTE, screenwidth,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        this.animator.setInterpolator(new LinearInterpolator());
        this.animator.setDuration(this.duration);
        this.animator.setFillAfter(true);
        this.animator.setRepeatMode(Animation.RESTART);
        this.animator.setRepeatCount(Animation.INFINITE);
        this.animator.setStartOffset(6000);
        setAnimationListener();
        this.simpleText = simpleText;

    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setScrollingText(String text) {
        if (simpleText!=null){
            this.scrollingTextView.setText(simpleText.formatLine(text, this.scrollingTextView, true));
        }else{
            this.scrollingTextView.setText(text);
        }

    }

    public void start() {
        this.scrollingTextView.setSelected(true);
        this.scrollingTextView.startAnimation(this.animator);
    }

    public void setAnimationListener() {

        animator.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                // This callback function can be used to perform any task at the end of the Animation
            }

            public void onAnimationRepeat(Animation animation) {
                if (!Variables.scrollTitle) animation.cancel();
            }
        });
    }
}
