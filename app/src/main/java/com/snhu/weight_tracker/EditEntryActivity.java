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
public class EditEntryActivity extends AppCompatActivity {

    AuthenticatedUser currentUser;
    WeightRecord editEntry;
    EditText dateEntryView, weightEntryView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        // get data from previous activity screen
        Intent incomingIntent = getIntent();
        currentUser = (AuthenticatedUser) incomingIntent.getSerializableExtra("currentUser");
        editEntry = (WeightRecord) incomingIntent.getSerializableExtra("editWeightRecord");

        dateEntryView = findViewById(R.id.editTextDate);
        weightEntryView = findViewById(R.id.editTextWeight);

        // get weight record date/timestamp and convert to simpler format
        String date = editEntry.getDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        String dateString = sdf.format(date);
        dateEntryView.setText(date);

        // get weight record weight
        float editWeight = editEntry.getWeight();

        weightEntryView.setText(String.valueOf(editWeight));

        Button btnCancel = findViewById(R.id.buttonCancel);
        Button btnSubmit = findViewById(R.id.buttonUpdate);

        // create click listeners for submit and cancel buttons
        btnCancel.setOnClickListener(l -> {
            // send user back to previous activity
            Intent intent = new Intent(EditEntryActivity.this, MainActivity.class);
            intent.putExtra("currentUser", (Serializable) currentUser);
            intent.putExtra("origin", "AddEntry");
            startActivity(intent);
        });

        btnSubmit.setOnClickListener(l -> {
            // validate user input and insert new entry
            if (validateInput()) {
                float newWeight = Float.parseFloat(weightEntryView.getText().toString());
                String newDate = dateEntryView.getText().toString();
                boolean editRecord = editWeightEntry(newWeight, newDate);
                if (editRecord) {
                    Intent intent = new Intent(EditEntryActivity.this, MainActivity.class);
                    intent.putExtra("currentUser", (Serializable) currentUser);
                    intent.putExtra("origin", "EditEntry");
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error modifying weight record.", Toast.LENGTH_SHORT);
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
    private boolean editWeightEntry(float newWeight, String newDate) {
        DBHandler db = new DBHandler(this);
        return db.editWeightRecord(editEntry.getId(), new WeightRecord(newWeight, newDate));
    }
}
