package com.kent.anim.demo.rxanim;


import com.kent.anim.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.Nullable;
import timber.log.Timber;

/**
 * Created by Kent Song on 2018/11/8.
 */
public class ScaleAnimActivity extends AppCompatActivity {

    private final String TAG = ScaleAnimActivity.class.getSimpleName();

    @BindView(R.id.target_img)
    ImageView mImgView;
    @BindView(R.id.btn_action1)
    Button btnAction1;

    private float temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_anim);
        ButterKnife.bind(this);

        mImgView = findViewById(R.id.target_img);
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScaleAnimActivity.this, "click view", Toast.LENGTH_SHORT).show();
            }
        });
        btnAction1 = findViewById(R.id.btn_action1);
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animCombo();
            }
        });
    }

    private void animCombo() {
        //属性动画
        AnimatorSet animatorSet = genAnimatorSet(mImgView);
        animatorSet.start();
        //View动画
//        AnimationSet animationSet = genAnimationSet(mImgView);
//        mImgView.startAnimation(animationSet);
    }

    //属性动画
    private AnimatorSet genAnimatorSet(View targetView) {
        //改变 scaleX
//        return genAnimatorSetPlan1(targetView);
//        改变 width、height
        return genAnimatorSetPlan2(targetView);
    }

    private AnimatorSet genAnimatorSetPlan2(View targetView) {

        /**
         * 利用插值器或 AnimationUpdateLinstener 来改变图片宽高
         *
         * **/
        int targetWidth = 400;
        int targetHeight = 300;
        float targetX = 200f;
        float targetY = 200f;

        Log.d(TAG, "desktopBootAd-genAnimation-targetWidth=" + targetWidth + ", targetHeight="
                + targetHeight);

        int videoViewWidth = targetView.getWidth();
        int videoViewHeight = targetView.getHeight();

        Log.d(TAG, "desktopBootAd-genAnimation-oriWidth=" + videoViewWidth + ", targetWidth="
                + targetWidth);
        Log.d(TAG, "desktopBootAd-genAnimation-oriHeight=" + videoViewHeight + ", targetHeight="
                + targetHeight);


        AnimatorSet animSet = new AnimatorSet();
//        ValueAnimator anim2 = ValueAnimator.ofInt(videoViewWidth, targetWidth);
//        ValueAnimator anim1 = ValueAnimator.ofInt(videoViewHeight, targetHeight);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(targetView, "x", 0f, targetX);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(targetView, "y", 0f, targetY);


//        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
//                Log.d("ScaleAnimActivity", "onAnimationUpdate  value="+value );
//
//                  //TODO 不断重绘效能低落
//                ViewGroup.LayoutParams params = targetView.getLayoutParams();
//                params.height = value;
//                targetView.requestLayout();
//            }
//        });

//        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
////                targetView.getLayoutParams().width = value;
//                if(value == targetWidth){
////                    targetView.requestLayout();
//                }
//            }
//        });
        animSet.setDuration(600);
        animSet.playTogether(/**anim1, anim2**/anim3, anim4);
        animSet.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                if (input == temp) {
                    return input;
                }
                temp = input;
                //TODO 不建议采用，不断重绘造成效能低落，并且逻辑没错系统还算错，有并发 requestLayout 情况
                Float width = 1920 - (1920 - targetWidth) * input;
                Float height = 1080 - (1080 - targetHeight) * input;
                Log.d("ScaleAnimActivity", "width =" + width);
                ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                layoutParams.height = height.intValue();
                layoutParams.width = width.intValue();
                targetView.setLayoutParams(layoutParams);
                targetView.requestLayout();
                Log.d("ScaleAnimActivity", "getInterpolation =" + input);
                return input;
            }
        });
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int w = targetView.getWidth();
                int h = targetView.getHeight();

                Log.d("ScaleAnimActivity", "end w=" + w + ",h=" + h);
                Log.d("ScaleAnimActivity", "x=" + targetView.getX());
                Log.d("ScaleAnimActivity", "y=" + targetView.getY());
                Log.d("ScaleAnimActivity", "PivotX=" + targetView.getPivotX());
                Log.d("ScaleAnimActivity", "PivotY=" + targetView.getPivotY());

                //动画结束时，有些许偏差需重定位
                targetView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                        layoutParams.height = targetHeight;
                        layoutParams.width = targetWidth;
                        targetView.setLayoutParams(layoutParams);
                        targetView.setX(targetX);
                        targetView.setY(targetY);
                        targetView.requestLayout();

                        Log.d("ScaleAnimActivity", "delay end w=" + w + ",h=" + h);
                        Log.d("ScaleAnimActivity", "x=" + targetView.getX());
                        Log.d("ScaleAnimActivity", "y=" + targetView.getY());
                        Log.d("ScaleAnimActivity", "PivotX=" + targetView.getPivotX());
                        Log.d("ScaleAnimActivity", "PivotY=" + targetView.getPivotY());
                    }
                }, 50);
            }

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                int w = targetView.getMeasuredWidth();
                int h = targetView.getMeasuredHeight();
                Log.d("ScaleAnimActivity", "start w=" + w + ",h=" + h);
                Log.d("ScaleAnimActivity", "x=" + targetView.getX());
                Log.d("ScaleAnimActivity", "y=" + targetView.getY());
                Log.d("ScaleAnimActivity", "PivotX=" + targetView.getPivotX());
                Log.d("ScaleAnimActivity", "PivotY=" + targetView.getPivotY());
            }


        });
        return animSet;
    }

    private AnimatorSet genAnimatorSetPlan1(View targetView) {
        float targetWidth = 400f;
        float targetHeight = 300f;


        /**
         * 属性动画改变View scaleX、scaleY，并不会改变 View 的 height、width
         * 在移动的过程中，要看成是原始大小的 View 在移动
         * 因此 (x,y) 需要做转换，因为视觉上 View 大小被缩小
         * **/
        float targetX = 500;
        float targetY = 500;

        //转换公式
        float transferX = targetX - 960 + targetWidth / 2;
        float transferY = targetY - 540 + targetHeight / 2;


        Log.d(TAG, "desktopBootAd-genAnimation-targetWidth=" + targetWidth + ", targetHeight="
                + targetHeight);

        int videoViewWidth = targetView.getWidth();
        int videoViewHeight = targetView.getHeight();
        float widthRate = targetWidth / videoViewWidth;
        float heightRate = targetHeight / videoViewHeight;

        Log.d(TAG, "desktopBootAd-genAnimation-oriWidth=" + videoViewWidth + ", targetWidth="
                + targetWidth + ", rate=" + widthRate);
        Log.d(TAG, "desktopBootAd-genAnimation-oriHeight=" + videoViewHeight + ", targetHeight="
                + targetHeight + ", rate=" + heightRate);


        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, widthRate);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, heightRate);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(targetView, "x", 0f, transferX);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(targetView, "y", 0f, transferY);

        animSet.setDuration(1200);
        animSet.playTogether(anim1, anim2, anim3, anim4);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int w = targetView.getMeasuredWidth();
                int h = targetView.getMeasuredHeight();
                Log.d("ScaleAnimActivity", "end w=" + w + ",h=" + h);
                Log.d("ScaleAnimActivity", "x=" + targetView.getX());
                Log.d("ScaleAnimActivity", "y=" + targetView.getY());
                Log.d("ScaleAnimActivity", "PivotX=" + targetView.getPivotX());
                Log.d("ScaleAnimActivity", "PivotY=" + targetView.getPivotY());
            }

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                int w = targetView.getMeasuredWidth();
                int h = targetView.getMeasuredHeight();
                Log.d("ScaleAnimActivity", "start w=" + w + ",h=" + h);
                Log.d("ScaleAnimActivity", "x=" + targetView.getX());
                Log.d("ScaleAnimActivity", "y=" + targetView.getY());
                Log.d("ScaleAnimActivity", "PivotX=" + targetView.getPivotX());
                Log.d("ScaleAnimActivity", "PivotY=" + targetView.getPivotY());
            }
        });
        return animSet;


        /**  另一种型态 **/
//        mImgView.animate()
//                .scaleXBy(1f)
//                .scaleX(widthRate)
//                .scaleYBy(1f)
//                .scaleY(heightRate)
//                .setDuration(1200)
//                .x(targetX)
//                .y(targetY)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        int w = mImgView.getMeasuredWidth();
//                        int h = mImgView.getMeasuredHeight();
//                        Log.d("ScaleAnimActivity","end w="+w+",h="+h);
//                    }
//
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        super.onAnimationStart(animation);
//                        int w = mImgView.getMeasuredWidth();
//                        int h = mImgView.getMeasuredHeight();
//                        Log.d("ScaleAnimActivity","start w="+w+",h="+h);
//                    }
//                });
//        mImgView.start();
    }


    //View动画
    private AnimationSet genAnimationSet(View targetView) {

        float targetX = 0;
        float targetY = 0;
        float targetWidth = 400;
        float targetHeight = 300;

        AnimationSet animSet = new AnimationSet(false);
        int videoViewWidth = targetView.getWidth();
        int videoViewHeight = targetView.getHeight();
        float widthRate = targetWidth / videoViewWidth;
        float heightRate = targetHeight / videoViewHeight;

        Log.d(TAG, "desktopBootAd-genAnimation-oriWidth=" + videoViewWidth + ", targetWidth="
                + targetWidth + ", rate=" + widthRate);
        Log.d(TAG, "desktopBootAd-genAnimation-oriHeight=" + videoViewHeight + ", targetHeight="
                + targetHeight + ", rate=" + heightRate);

        /**
         * ScaleAnimation构造
         * @param fromX X方向开始时的宽度，1f表示控件原有大小
         * @param toX X方向结束时的宽度，
         * @param fromY Y方向上开的宽度，
         * @param toY Y方向结束的宽度
         * @param pivotX 缩放的轴心X的位置，取值类型是float，单位是px像素，比如：X方向控件中心位置是 mVideoView.getWidth() / 2f
         * @param pivotY 缩放的轴心Y的位置，取值类型是float，单位是px像素，比如：X方向控件中心位置是 mVideoView.getHeight() / 2f
         **/
        ScaleAnimation scaleAnim = new ScaleAnimation(1f, widthRate, 1f, heightRate, targetX,
                targetY);
        scaleAnim.setDuration(1200);

        animSet.addAnimation(scaleAnim);
        animSet.setFillBefore(false);
        animSet.setFillAfter(true);

        return animSet;
    }


    public Completable enlarge(final View view) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                Timber.d(">> enlarge subscribe");
                AnimatorSet animSet = new AnimatorSet();

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2.1f, 2.08f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2.1f, 2.08f);
                animSet.setDuration(1000);
                animSet.playTogether(scaleX, scaleY);
                animSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Timber.d(">> enlarge onAnimationEnd");
                        emitter.onComplete();
                    }
                });
                animSet.start();
            }
        });


    }

    public Completable shrink(final View view) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                Timber.d(">> shrink subscribe");

                AnimatorSet animSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 2.08f, 0.99f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 2.08f, 0.99f, 1f);
                animSet.setDuration(1000);
                animSet.setInterpolator(new DecelerateInterpolator());
                animSet.play(scaleX).with(scaleY);
                animSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Timber.d(">> shrink onAnimationEnd");
                        emitter.onComplete();
                    }
                });
                animSet.start();
            }
        });
    }


    public static void launch(Context context) {
        Intent intent = new Intent(context, ScaleAnimActivity.class);
        context.startActivity(intent);
    }
}
