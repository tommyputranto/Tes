package com.androbos.tes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    private Button signup;
    private EditText fullName;
    private EditText phoneNumber;
    private EditText emailAdress;
    private EditText passwordSignup;
    private EditText retypePassword;

    private String fullNametxt;
    private String phoneNumbertxt;
    private String emailAddresstxt;
    private String passwordSignuptxt;
    private String retypePasswordtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup = (Button)findViewById(R.id.signupBtn);
        fullName=  (EditText)findViewById(R.id.fullName);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        emailAdress = (EditText)findViewById(R.id.emailAddress);
        passwordSignup = (EditText)findViewById(R.id.passwordSignup);
        retypePassword = (EditText)findViewById(R.id.retypePassword);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullNametxt = fullName.getText().toString();
                phoneNumbertxt = phoneNumber.getText().toString();
                emailAddresstxt  = emailAdress.getText().toString();
                passwordSignuptxt = passwordSignup.getText().toString();
                retypePasswordtxt = retypePassword.getText().toString();
                if (fullNametxt.equals("") && phoneNumbertxt.equals("") && emailAddresstxt.equals("")
                        && passwordSignuptxt.equals("") && retypePasswordtxt.equals("")){
                    Toast.makeText(getApplicationContext(),"Please complete the Sign Up form",Toast.LENGTH_LONG).show();
                }
                else {
                    if (passwordSignuptxt.equals(retypePasswordtxt)){
                        ParseUser user = new ParseUser();
                        user.setUsername(emailAddresstxt);
                        user.setEmail(emailAddresstxt);
                        user.setPassword(passwordSignuptxt);
                        user.put("fullName", fullNametxt);
                        user.put("phone", phoneNumbertxt);
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // Show a simple Toast message upon successful registration
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Signed up, please log in.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Sign up Error", Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"retype your password",Toast.LENGTH_LONG).show();
                    }


                }
            }
        });

    }


}
