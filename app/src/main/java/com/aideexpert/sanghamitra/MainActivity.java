package com.aideexpert.sanghamitra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.Button;
import android.widget.TextView;

import com.aideexpert.sanghamitra.activity.LoginActivity;
import com.aideexpert.sanghamitra.helper.SQLiteHandler;
import com.aideexpert.sanghamitra.helper.SessionManager;
import com.onesignal.OneSignal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/*
 * Demo of creating an application to open any URL inside the application and clicking on any link from that URl
should not open Native browser but  that URL should open in the same screen.
 */
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPassword;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;
    WebView web;
    String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtPassword = (TextView) findViewById(R.id.password);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        final String email = user.get("email");
        final String password = user.get("password");

        // Displaying the user details on the screen
//        txtName.setText(name);
//        txtEmail.setText(email);
//        txtPassword.setText(password);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        web = (WebView) findViewById(R.id.webview01);
     //  Button btnLogout = new Button(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
    // btnLogout.setText("Button");
      //btnTag.setId(1);
        //web.addView(btnLogout);
      //  setContentView(web);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onJsAlert(WebView view, String url, String message, JsResult result)
            { return super.onJsAlert(view, url, message, result); } });


        //web.setHttpAuthUsernamePassword("aideexpert.com", "", email, password);
        //String s = "https://www.aideexpert.com/dashboard?username=" + email;
        //String urlWithData = "https://www.aideexpert.com/dashboard" + "?username=" + email + "&password="+password;
        //web.loadUrl(urlWithData);
//web.loadUrl("javascript:window.MyHandler.setResult( https://www.aideexpert.com/dashboard ("+email+","+password+") )");
        //Log.e("s","hi");
        //web.loadUrl(s);
        web.loadUrl("https://www.aideexpert.com/session?email="+email);


        // url="https://www.aideexpert.com/dashboard";
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        JSONObject tags = new JSONObject();
        try {
            tags.put("email", email);
            tags.put("username", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
        //textView.setText("Tags sent!");


    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

