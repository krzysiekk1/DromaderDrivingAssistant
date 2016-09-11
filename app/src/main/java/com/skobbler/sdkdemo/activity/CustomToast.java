package com.skobbler.sdkdemo.activity;

/**
 * Created by marcinsendera on 01.09.16.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import com.skobbler.sdkdemo.R;

public class CustomToast {

    public static final int       LONG_DELAY       = 3500;
    public static final int       SHORT_DELAY      = 2000;
    public static final int       VERY_SHORT_DELAY = 1000;

    private View                  mToastView;
    private TextView              mMessageView;
    private TextView              mActionView;
    private ViewPropertyAnimator  mToastAnimator;
    private Handler               mHideHandler     = new Handler();
    private OnToastActionListener mActionListener;
    private Bundle                mActionParams;
    private String                mMessage;
    private int                   mLeftDrawableRes;
    private String                mActionMessage;
    private long                  mDuration;

    public interface OnToastActionListener {

        void onToastAction(Bundle object);
    }

    public CustomToast(View view, OnToastActionListener actionListener) {

        mToastView = view;
        mToastAnimator = mToastView.animate();
        mActionListener = actionListener;
        mMessageView = (TextView) mToastView.findViewById(R.id.customToastMessage);
        mActionView = (TextView) mToastView.findViewById(R.id.customToastAction);
        mActionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                hideCustomToast(false);
                mActionListener.onToastAction(mActionParams);
            }
        });
        hideCustomToast(true);
    }

    /*
     * Just displays a simple toast with a message and an icon and an action
     */
    public void showMessage(boolean immediate, String message, int leftDrawableRes, String actionMessage, Bundle actionParams, long duration) {

        if (!TextUtils.isEmpty(message)) {
            mMessage = message;
            mLeftDrawableRes = leftDrawableRes;
            mActionMessage = actionMessage;
            mActionParams = actionParams;
            mDuration = duration;

            mMessageView.setText(mMessage);

            mHideHandler.removeCallbacks(mHideRunnable);
            mHideHandler.postDelayed(mHideRunnable, mDuration);
            if (leftDrawableRes != 0) {
                mMessageView.setCompoundDrawablesWithIntrinsicBounds(mLeftDrawableRes, 0, 0, 0);
            }
            else {
                mMessageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            if (!TextUtils.isEmpty(mActionMessage)) {
                mActionView.setVisibility(View.VISIBLE);
                mActionView.setText(mActionMessage);
            }
            else {
                mActionView.setVisibility(View.GONE);
                mMessageView.setGravity(Gravity.CENTER);
            }
            mToastView.setVisibility(View.VISIBLE);
            if (immediate) {
                mToastView.setAlpha(1);
            }
            else {
                mToastAnimator.cancel();
                mToastAnimator.alpha(1).setDuration(mToastView.getResources().getInteger(android.R.integer.config_shortAnimTime)).setListener(null);
            }
        }
    }

    /*
     * Just displays a simple toast with a message
     */
    public void showMessage(boolean immediate, String message, long duration) {

        if (!TextUtils.isEmpty(message)) {
            mMessage = message;
            mDuration = duration;
            mLeftDrawableRes = 0;
            mActionMessage = null;
            mActionParams = null;

            mMessageView.setText(mMessage);
            mMessageView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mMessageView.setGravity(Gravity.CENTER);

            mHideHandler.removeCallbacks(mHideRunnable);
            mHideHandler.postDelayed(mHideRunnable, mDuration);
            mActionView.setVisibility(View.GONE);
            mToastView.setVisibility(View.VISIBLE);
            if (immediate) {
                mToastView.setAlpha(1);
            }
            else {
                mToastAnimator.cancel();
                mToastAnimator.alpha(1).setDuration(mToastView.getResources().getInteger(android.R.integer.config_shortAnimTime)).setListener(null);
            }
        }
    }

    /*
     * Just displays a simple toast with a message and an icon
     */
    public void showMessage(boolean immediate, String message, int leftDrawableRes, long duration) {

        if (!TextUtils.isEmpty(message)) {
            mMessage = message;
            mDuration = duration;
            mLeftDrawableRes = leftDrawableRes;
            mActionMessage = null;
            mActionParams = null;

            mMessageView.setText(mMessage);
            mMessageView.setCompoundDrawablesWithIntrinsicBounds(leftDrawableRes, 0, 0, 0);
            mMessageView.setGravity(Gravity.CENTER);

            mHideHandler.removeCallbacks(mHideRunnable);
            mHideHandler.postDelayed(mHideRunnable, mDuration);
            mActionView.setVisibility(View.GONE);
            mToastView.setVisibility(View.VISIBLE);
            if (immediate) {
                mToastView.setAlpha(1);
            }
            else {
                mToastAnimator.cancel();
                mToastAnimator.alpha(1).setDuration(mToastView.getResources().getInteger(android.R.integer.config_shortAnimTime)).setListener(null);
            }
        }
    }

    public void hideCustomToast(boolean immediate) {

        mHideHandler.removeCallbacks(mHideRunnable);
        if (immediate) {
            mToastAnimator.cancel();
            mToastView.setVisibility(View.GONE);
            mToastView.setAlpha(0);
            clearData();
        }
        else {
            mToastAnimator.cancel();
            mToastAnimator.alpha(0).setDuration(mDuration).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {

                    mToastView.setVisibility(View.GONE);
                    clearData();
                }
            });
        }
    }

    private void clearData() {

        mActionParams = null;
        mActionMessage = null;
        mMessage = null;
        mLeftDrawableRes = 0;
        mDuration = 0;
    }

    public void onSaveInstanceState(Bundle outState) {

        outState.putString("message", mMessage);
        outState.putInt("left_drawable_resource", mLeftDrawableRes);
        outState.putString("action_message", mActionMessage);
        outState.putBundle("action_bundle", mActionParams);
        outState.putLong("duration", mDuration);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mMessage = savedInstanceState.getString("message");
            mLeftDrawableRes = savedInstanceState.getInt("left_drawable_resource");
            mActionMessage = savedInstanceState.getString("action_message");
            mActionParams = savedInstanceState.getBundle("action_bundle");
            mDuration = savedInstanceState.getLong("duration");

            if (mActionParams != null) {
                showMessage(true, mMessage, mLeftDrawableRes, mActionMessage, mActionParams, mDuration);
            }
            else {
                showMessage(true, mMessage, mDuration);
            }
        }
    }

    public View getView() {

        return mToastView;
    }

    private Runnable mHideRunnable = new Runnable() {

        @Override
        public void run() {

            hideCustomToast(false);
        }
    };
}