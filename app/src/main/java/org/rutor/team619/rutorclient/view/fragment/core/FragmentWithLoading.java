package org.rutor.team619.rutorclient.view.fragment.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.util.Objects;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;

import retrofit.RetrofitError;

/**
 * Created by BORIS on 12.11.2015.
 */
public abstract class FragmentWithLoading extends InjectableFragment {

    private static final String TAG = FragmentWithLoading.class.getName() + ":";
    protected volatile boolean isRunning = false;
    protected volatile int currentOrientation = -1;

    protected void blockRotation(final boolean block) {
        if (block) {
//            currentOrientation = this.getActivity().getRequestedOrientation();
//            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            int rotation = this.getActivity().getWindowManager().getDefaultDisplay().getRotation();

            switch (rotation) {
                case Surface.ROTATION_180:
                    this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_0:
                    this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }

        } else {
//            if(currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//                this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            } else if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//                this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    protected void loadData() {
        if (isRunning) {
            return;
        }

        new Handler().postDelayed(() -> {
            showProgress(true);

            if (getRepository() == null) {
                return;
            }

            try {
                loadDataProcess();
            } catch (Exception e) {
                Log.e(TAG, "ERROR:", e);
            }
        }, 100);
    }

    protected abstract void loadDataProcess();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        if (getActivity() != null) getActivity().runOnUiThread(() -> {
            try {
                blockRotation(show);
            } catch (Exception e) {
                Log.e(TAG, "ERROR:", e);
            }

            try {
                showProgressData(show);
            } catch (Exception e) {
                Log.e(TAG, "ERROR:", e);
            }
        });
    }

    protected void showProgressData(final boolean show) {
        if (Objects.isNull(getMainPane())) return;
        if (Objects.isNull(getProgressView())) return;
        if (Objects.isNull(getErrorPane())) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            getMainPane().setVisibility(show ? View.GONE : View.VISIBLE);
            getMainPane().animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            getMainPane().setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

            getProgressView().setVisibility(show ? View.VISIBLE : View.GONE);
            getProgressView().animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            getProgressView().setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            getProgressView().setVisibility(show ? View.VISIBLE : View.GONE);
            getMainPane().setVisibility(show ? View.GONE : View.VISIBLE);
        }

        if (show) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> isRunning = show, 500);
        } else {
            isRunning = show;
        }

        getErrorPane().setVisibility(View.GONE);
    }

    protected void showError(final Throwable e) {
        Log.e(TAG, "ERROR: ", e);
        if (getActivity() != null) getActivity().runOnUiThread(() -> {
            try {
                showErrorData(e);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    protected void showErrorData(Throwable e) {
        showProgress(false);
        getErrorPane().setVisibility(View.VISIBLE);
        getMainPane().setVisibility(View.GONE);

        try {
            if (e instanceof RetrofitError) {
                if (((RetrofitError) e).getKind() == RetrofitError.Kind.NETWORK) {
                    getErrorType().setImageResource(R.drawable.ic_signal_wifi_off_white_48dp);
                }
            } else {
                if (e.getMessage() != null) {
                    Toast.makeText(getContext(), "error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }

            if (e.getMessage() != null) {
                getErrorPaneStackTrace().setText(e.getClass().getSimpleName() + ":" + e.getMessage().toString());
            } else {
                getErrorPaneStackTrace().setText(e.getClass().getSimpleName() + ":" + e);
            }
        } catch (Exception e1) {
            Log.e(TAG, "showErrorData#Error: ", e1);
            getErrorPane().setVisibility(View.GONE);
            getMainPane().setVisibility(View.VISIBLE);
        }
    }

    protected abstract View getMainPane();

    protected abstract View getProgressView();

    public abstract Context getContext();

    protected abstract RuTorRepository getRepository();

    protected abstract Settings getProjectSettings();

    protected abstract DefaultAdapter getAdapter();

    protected abstract View getErrorPane();

    protected abstract TextView getErrorPaneStackTrace();

    protected abstract ImageView getErrorType();

}
