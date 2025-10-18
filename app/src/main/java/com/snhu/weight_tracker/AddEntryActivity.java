package com.snhu.weight_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Activity screen for adding a new weight entry into the journal
 */
public class AddEntryActivity extends AppCompatActivity {

    AuthenticatedUser currentUser;
    EditText dateEntryView, weightEntryView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // get data from previous activity screen
        Intent incomingIntent = getIntent();
        currentUser = (AuthenticatedUser) incomingIntent.getSerializableExtra("currentUser");

        dateEntryView = findViewById(R.id.editTextDate);
        weightEntryView = findViewById(R.id.editTextWeight);

        // get current date/timestamp and convert to simpler format
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = sdf.format(date);
        dateEntryView.setText(dateString);

        Button btnCancel = findViewById(R.id.buttonCancel);
        Button btnSubmit = findViewById(R.id.buttonUpdate);

        // create click listeners for submit and cancel buttons
        btnCancel.setOnClickListener(l -> {
            // send user back to previous activity
            Intent intent = new Intent(AddEntryActivity.this, MainActivity.class);
            intent.putExtra("currentUser", (Serializable) currentUser);
            intent.putExtra("origin", "AddEntry");
            startActivity(intent);
        });

        btnSubmit.setOnClickListener(l -> {
            // validate user input and insert new entry
            if (validateInput()) {
                float newWeight = Float.parseFloat(weightEntryView.getText().toString());
                String newDate = dateEntryView.getText().toString();
                long newEntry = addWeightEntry(newWeight, newDate);
                if (newEntry != -1) {
                    Intent intent = new Intent(AddEntryActivity.this, MainActivity.class);
                    intent.putExtra("currentUser", (Serializable) currentUser);
                    intent.putExtra("origin", "AddEntry");
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error adding new entry.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    /**
     * Check new entry input
     * @return whether or not user input is valid
     */
    private boolean validateInput() {
        return (dateEntryView.getText().length() > 0 && weightEntryView.getText().length() > 0);
    }

    /**
     * Create new entry in weight journal DB
     *
     * @param newWeight   new body weight
     * @param newDate     date new body weight was measured
     * @return            whether new entry was successfully added
     */
    private long addWeightEntry(float newWeight, String newDate) {
        DBHandler db = new DBHandler(this);
        return db.addWeightRecord(currentUser.get_id(), new WeightRecord(newWeight, newDate));
    }
}
