package com.example.fithub;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.fithub.ModelClasses.UserInformation;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    UserInformation userInfo = new UserInformation();
    private EditText emailTV, passwordTV, fNameTV, lNameTV, ageTV, dobTV;
    private Button regButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private int age = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("User Information");//where user info is stored under fithub db
        initializeUI();
        //if user clicks register run registerNewUser method
        regButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }
    /*
    registers a new user with information they provide
    @param NA
    @return NA
     */
    private void registerNewUser()
    {
        String email, password, fName, lName, dob, strAge;

        email = emailTV.getText().toString().trim();
        password = passwordTV.getText().toString().trim();
        fName = fNameTV.getText().toString().trim();
        lName = lNameTV.getText().toString().trim();
        dob = dobTV.getText().toString().trim();
        strAge = ageTV.getText().toString().trim();
        //check if age from strAge is a valid entry
        try
        {
            age = Integer.parseInt(strAge) ;
        }
        catch (NumberFormatException e){}
        //if any of the EditText fields are empty prompt user for information
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "Please Enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(), "Please Enter password...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(fName))
        {
            Toast.makeText(getApplicationContext(), "Please Enter first name...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(lName))
        {
            Toast.makeText(getApplicationContext(), "Please Enter last name...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(dob))
        {
            Toast.makeText(getApplicationContext(), "Please Enter date of birth...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(strAge))
        {
            Toast.makeText(getApplicationContext(), "Please Enter date of Age...", Toast.LENGTH_LONG).show();
            return;
        }
        //create new user with user provided variables
        userInfo = new UserInformation(fName, lName, email, dob, age);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();

                        }else
                        {
                            //getting unique user id
                            String user_id = mAuth.getInstance().getCurrentUser().getUid();
                            myRef = myRef.child(user_id);
                            //nesting account details into user id
                            myRef.child("First Name").setValue(fName);
                            myRef.child("Last Name").setValue(lName);
                            myRef.child("Date of Birth").setValue(dob);
                            myRef.child("Email").setValue(email);
                            myRef.child("Age").setValue(age);

                            Toast.makeText(getApplicationContext(),"Registration Successful, Verification Email Sent!!", Toast.LENGTH_LONG).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //send user email verification
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Log.i("Success", "Yes");
                                    }else{
                                        Log.i("Success", "No");
                                    }
                                }
                            });
                            finish();
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);//after registration send to login
                        }
                    }
                });
    }
    /*Initializes class variables and links them to xml
    @param NA
    @returns NA*/
    private void initializeUI()
    {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);
        fNameTV = findViewById(R.id.userName);
        lNameTV = findViewById(R.id.lastname);
        ageTV = findViewById(R.id.age);
        dobTV = findViewById(R.id.birthday);
        regButton = findViewById(R.id.register);
    }
}
