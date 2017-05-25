package com.ivelsproject.ivelsid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ivelsproject.ivelsid.worker.LoginWorker;

public class LoginActivity extends AppCompatActivity implements LoginWorker.LoginCallbacks {

    private static final String TAG_TASK_FRAGMENT = "login_task_fragment";

    private LoginWorker loginWorker;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        }

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWorker.start(username.getText().toString(), password.getText().toString());
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        loginWorker = (LoginWorker) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        if (loginWorker == null) {
            loginWorker = new LoginWorker();
            fm.beginTransaction().add(loginWorker, TAG_TASK_FRAGMENT).commit();
        }

        if (loginWorker.isRunning()) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Validating...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    public void onLoginStart() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Validating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onLoginCancelled() {
        progressDialog.hide();
    }

    @Override
    public void onLoginFinish(boolean isUserValid) {
        progressDialog.hide();
        if (isUserValid) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Wrong username or password!");
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
