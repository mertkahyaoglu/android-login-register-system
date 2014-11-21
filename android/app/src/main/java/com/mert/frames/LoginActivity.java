package com.mert.frames;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends ActionBarActivity {

    private TextView title;
    private TextView signup;
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private String url = "http://10.0.2.2:8080/andro/login";

    private static String KEY_SUCCESS = "success";
    private static String KEY_USERID  = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        SignUpActivity.class);
                startActivity(i);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(et_email.getText()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText()).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter an invalid email", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(et_password.getText()) || et_password.getText().length() < 8 || et_password.getText().length() > 32) {
                    Toast.makeText(getApplicationContext(), "Your password must contain 8-32 character.", Toast.LENGTH_LONG).show();
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
                                                Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                                home.putExtra(KEY_USERID, Integer.parseInt(response.getString(KEY_USERID)));
                                                startActivity(home);
                                                finish();
                                            } else if (success == 0) {
                                                Toast.makeText(getApplicationContext(), R.string.invalid_login, Toast.LENGTH_LONG).show();
                                            } else {
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
                            params.put("tag", "login");
                            params.put("email", et_email.getText().toString());
                            params.put("password", Utils.md5(et_password.getText().toString()));
                            return params;
                        }

                    };

                    VolleyController.getInstance(getApplicationContext()).addToRequestQueue(rq);
                }
            }

        });



    }

    private void initUI() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setTypeface(Utils.getFont("nexa-bold.ttf", getApplicationContext()));
        signup = (TextView) findViewById(R.id.signup);
        et_email = (EditText)findViewById(R.id.let_email);
        et_email.setTypeface(Utils.getFont("Roboto-Thin.ttf", getApplicationContext()));
        et_password = (EditText)findViewById(R.id.let_password);
        et_password.setTypeface(Utils.getFont("Roboto-Thin.ttf", getApplicationContext()));
        btn_login = (Button)findViewById(R.id.btn_login);
    }
}
