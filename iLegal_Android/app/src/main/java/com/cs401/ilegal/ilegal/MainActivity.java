package com.cs401.ilegal.ilegal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class MainActivity extends Activity {

    EditText email;
    EditText password;
    TextView createAccount;
    TextView forgotPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email= (EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        createAccount=(TextView) findViewById(R.id.create_account);
        forgotPassword=(TextView) findViewById(R.id.forgot_password);
        loginButton = (Button) findViewById(R.id.login_button);


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CreateAccountActivity.class);
                startActivity(i);
            }

        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(email.getText()) && password.getText().length()>=6)
                {
                    Log.d("this is the password",HashPassword.encryptPassword(password.getText().toString()));

                    final HashMap<String,String> loginInfo = new HashMap<String,String>();
                    loginInfo.put("Username",email.getText().toString());
                    loginInfo.put("Password",HashPassword.encryptPassword(password.getText().toString()));

                    new AsyncTask<Integer, Void, JSONObject>(){

                        @Override
                        protected JSONObject doInBackground(Integer... params) {
                            JSONObject resultJson=new JSONObject();

                            String response= CreateAccountActivity.performPostCall("http://159.203.67.188:8080/Dev/SignIn",loginInfo);

                            try {
                                resultJson = new JSONObject(response);
                                Log.d("Result JSONN",resultJson.toString(1));

                                UserSingleton user = UserSingleton.getInstance();

                                user.setFirstName(resultJson.getString("FirstName"));
                                user.setLastName(resultJson.getString("LastName"));
                                user.setEmail(resultJson.getString("EmailAddress"));
                                user.setAddress1(resultJson.getString("Address1"));
                                user.setAddress2(resultJson.getString("Address2"));
                                user.setCity(resultJson.getString("City"));
                                user.setState(resultJson.getString("State"));
                                user.setPostalCode(resultJson.getString("PostalCode"));
                                user.setPhoneNumber(resultJson.getString("PhoneNumber"));
                                user.setDob(resultJson.getString("DOB"));
                                user.setLicenseNumber(resultJson.getString("LicenseNumber"));
                                user.setID(resultJson.getString("UserId"));

                                Log.d("singleton",user.toString());



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return resultJson;
                        }


                        @Override
                        protected void onPostExecute(JSONObject resultJson) {
                            super.onPostExecute(resultJson);

                            try {
                                if(resultJson.getString("Success").equals("true"))
                                {

                                    Intent i = new Intent(MainActivity.this,MainNavigationActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                                else
                                {
                                        if(resultJson.getString("Success").equals("false"))
                                        {
                                            Toast.makeText(MainActivity.this, "Wrong email/password!", Toast.LENGTH_LONG).show();
                                        }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute(1, 2, 3, 4, 5);

            }
                else Toast.makeText(MainActivity.this,"Make sure your email and password are correct.",Toast.LENGTH_LONG).show();
            }
        });
    }







    //function to validate email format
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
