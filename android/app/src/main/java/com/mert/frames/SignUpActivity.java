package com.mert.frames;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mert.frames.app.VolleyController;
import com.mert.frames.helpers.CustomJSONObjectRequest;
import com.mert.frames.helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends ActionBarActivity {

    private Button btn_register;
    private EditText et_email;
    private EditText et_username;
    private EditText et_password;

    private String url = "http://10.0.2.2:8080/andro/register";
    private static String KEY_SUCCESS = "success";
    private static String KEY_USERID  = "userid";
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_signup);
        initUI();

        thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                    Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(et_email.getText()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter an invalid email", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(et_password.getText()) || et_password.getText().length() < 8 || et_password.getText().length() > 32) {
                    Toast.makeText(getApplicationContext(), "Your password must contain 8-32 character.", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(et_username.getText()) || et_username.getText().length() < 2 || et_password.getText().length() > 32) {
                    Toast.makeText(getApplicationContext(), "Your username must contain 2-32 character.", Toast.LENGTH_LONG).show();
                }
                else{
                    CustomJSONObjectRequest rq = new CustomJSONObjectRequest(Request.Method.POST, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString(KEY_SUCCESS) != null) {
                                            int success = Integer.parseInt(response.getString(KEY_SUCCESS));
                                            if (success == 1) {
                                                Toast.makeText(getApplicationContext(), R.string.registered, Toast.LENGTH_LONG).show();
                                                thread.start();
                                            } else if (success == 0) {
                                                Toast.makeText(getApplicationContext(), R.string.email_exists, Toast.LENGTH_LONG).show();
                                            }else if (success == 2) {
                                                Toast.makeText(getApplicationContext(), R.string.username_exists, Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(getApplicationContext(), R.string.invalid_post, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Response Error", error.toString());
                            Toast.makeText(getApplicationContext(), R.string.invalid_post, Toast.LENGTH_LONG).show();
                        }
                    }) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/x-www-form-urlencoded");
                            return headers;
                        }

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("tag", "register");
                            params.put("email", et_email.getText().toString());
                            params.put("username", et_email.getText().toString());
                            params.put("password", Utils.md5(et_password.getText().toString()));
                            return params;
                        }

                    };

                    VolleyController.getInstance(getApplicationContext()).addToRequestQueue(rq);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        finish();
        return;
    }

    private void initUI() {
        btn_register = (Button) findViewById(R.id.btn_register);
        et_email = (EditText) findViewById(R.id.ret_email);
        et_username = (EditText) findViewById(R.id.ret_username);
        et_password = (EditText) findViewById(R.id.ret_password);
    }

}
