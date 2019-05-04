package com.example.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fithub.ModelClasses.Weight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Activity used to log weight into firebase database, and also graph.
 */
public class WeightLoggerActivity extends AppCompatActivity {
    /**
     * Instantiating inital buttons for the xml, also creating reference to Firebase object, authentication
     * and database reference. Creating reference to graphview object
     */
    private EditText date, weight;
    private Button log;
    private GraphView graphView;
    private LineGraphSeries series;
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        //instantiating xml and getting current user authorization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_logger);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //setting up a reference to the database
        db = FirebaseDatabase.getInstance();
        String user_id = user.getUid();
        //database subclass and child nodes
        myRef = db.getReference("User Weight").child(user_id);
        //initialize the buttons as well as graph function
        initializeUI();
        series = new LineGraphSeries();
        graphView.addSeries(series);

        //click the button to input data

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override

            //function for what to do when the reference to the database is called.
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[((int) dataSnapshot.getChildrenCount())];
                int x=0;

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Weight value = ds.getValue(Weight.class);
                    Date date1 =null;
                    if(date1!=null)
                    {
                        try {
                            date1 = new SimpleDateFormat("mm/dd/yy"   ).parse(value.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        return;
                    }

                    dp[x] = new DataPoint(date1, value.getWeight());
                    x++;
                }

                series.resetData(dp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("on cancelled", "Failed to read value.", databaseError.toException());

            }
        });
    }

    /*
      function that sets the weight and date object to be passed into the firebase database
     */
    private void setLog()
    {   double wght = 0.0;
        String inputDate = date.getText().toString();
        String getDouble = weight.getText().toString();
        try
        {
            wght = Double.parseDouble(getDouble);

        }catch(NumberFormatException e){}

        if(TextUtils.isEmpty(inputDate))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Date...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(getDouble))
        {
            Toast.makeText(getApplicationContext(), "Please Enter Weight...", Toast.LENGTH_LONG).show();
            return;
        }


        Weight w = new Weight(inputDate, wght);

        myRef.push().setValue(w);
        Toast.makeText(getApplicationContext(), "Weight Logged", Toast.LENGTH_LONG).show();
    }

    /*Initializes class variables and links them to xml
    @param NA
    @returns NA*/
    private void initializeUI()
    {
        date = findViewById(R.id.etDate);
        weight = findViewById(R.id.etWeight);
        log = findViewById(R.id.logBtn);
        graphView = findViewById(R.id.graph);
    }

}
