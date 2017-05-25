package com.ivelsproject.ivelsid.worker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;

public class LoginWorker extends Fragment {

    protected String username;
    protected String password;

    public interface LoginCallbacks {
        void onLoginStart();
        void onLoginCancelled();
        void onLoginFinish(boolean isUserValid);
    }

    private LoginCallbacks loginCallbacks;
    private LoginTask loginTask;
    private boolean isRunning;

    @Override
    public void onAttach(Activity activity) {
        Log.i("worker", "onAttach(Activity)");
        super.onAttach(activity);
        if (!(activity instanceof LoginCallbacks)) {
            throw new IllegalStateException("Activity " + activity.getLocalClassName() + " must implement the LoginCallbacks interface.");
        }

        loginCallbacks = (LoginCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }

    public void start(String username, String password) {
        if (!isRunning) {
            this.username = username;
            this.password = password;
            loginTask = new LoginTask();
            loginTask.execute();
            isRunning = true;
        }
    }

    public void cancel() {
        if (isRunning) {
            loginTask.cancel(false);
            loginTask = null;
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            loginCallbacks.onLoginStart();
            isRunning = true;
        }

        @Override
        protected Void doInBackground(Void... ignore) {
            for (int i = 0; !isCancelled() && i < 50; i++) {
                SystemClock.sleep(50);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            loginCallbacks.onLoginCancelled();
            isRunning = false;
        }

        @Override
        protected void onPostExecute(Void ignore) {
            loginCallbacks.onLoginFinish(true);
            isRunning = false;
        }
    }
}