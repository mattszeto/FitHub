package com.example.fithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fithub.ModelClasses.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * The type Workout details log activity.
 */
public class WorkoutDetailsLogActivity extends AppCompatActivity {
    //Initializing the references to objects, and xml buttons, etc.
    private EditText workdat, worktype, wExercise, workReps;
    private BottomNavigationView nav;
    private Button logger;
    private Button viewWorkouts;
    private FirebaseUser user;
    private  DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //linking to xml layout as well as initializing the references.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details_log);
        initializeUI();
        //getting the current user info
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.navigation_home:
                        Intent home = new Intent(WorkoutDetailsLogActivity.this, MainActivity.class);
                        startActivity(home);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.navigation_log:
                        break;
                    case R.id.navigation_account:
                        Intent acct = new Intent(WorkoutDetailsLogActivity.this, AccountActivity.class);
                        startActivity(acct);
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
        //getting instance of the database to be manipulated, as well as a reference to
        //that database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        myRef = db.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Workout value = dataSnapshot.getValue(Workout.class);
                Log.d("Val", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("on cancelled", "Failed to read value.", error.toException());
            }
        });
        //Button to switch to view of all the logged workouts
        viewWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (WorkoutDetailsLogActivity.this, DisplayWorkoutsActivity.class);
                startActivity(intent);
            }
        });

        logger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLog();
                clearText();

            }
        });

    }

    /**
     * function to set the date, type of workout, specific exercise, and reps
     */
    private void setLog()
    {
        String d;
        String type;
        String time;
        String reps;
        int repetitions = 0;

        d = workdat.getText().toString().trim();
        type = worktype.getText().toString().trim();
        time = wExercise.getText().toString().trim();
        reps = workReps.getText().toString().trim();
        try{
            repetitions = Integer.parseInt(reps);
        }
        catch(NumberFormatException e)
        {

        }

        if(TextUtils.isEmpty(d))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Date...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(type))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Type...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(time))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Exercise...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(reps))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Repetitions...", Toast.LENGTH_LONG).show();
            return;

        }

        Workout w = new Workout(d,type,time,repetitions);

        String user_id = user.getUid();
        //Storing into realtime Firebase Database under Workouts -> Unique User ID
        Toast.makeText(getApplicationContext(), "Workout Logged", Toast.LENGTH_LONG).show();
        myRef.child("Workouts").child(user_id).push().setValue(w);
    }

    //initialize buttons etc.
    private void initializeUI()
    {
        logger = findViewById(R.id.logBtn);
        workdat = findViewById(R.id.etDate);
        worktype = findViewById(R.id.type);
        wExercise =  findViewById(R.id.exercise);
        workReps = findViewById(R.id.reps);
        viewWorkouts = findViewById(R.id.viewWorkouts);
        nav = findViewById(R.id.navigation);
    }

/*
    clears edit text fields
    @param NA
    @return NA
     */
    public void clearText()
    {
        workdat.setText("");
        worktype.setText("");
        wExercise.setText("");
        workReps.setText("");
    }
}
