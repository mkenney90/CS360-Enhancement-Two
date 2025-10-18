package com.snhu.weight_tracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // initialize activity views for displaying user weight data
    private TableLayout tl;
    private LinearLayout sv;
    AuthenticatedUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tl = (TableLayout) findViewById(R.id.tableWeightData);
        sv = (LinearLayout) findViewById(R.id.scrollableView);
        Button btnAddEntry = findViewById(R.id.buttonAddEntry);
        Button btnLogout = findViewById(R.id.buttonLogout);

        Intent incomingIntent = getIntent();
        String intentOrigin = incomingIntent.getStringExtra("origin");
        currentUser = (AuthenticatedUser) incomingIntent.getSerializableExtra("currentUser");

        Toast toast;
        if (currentUser != null) {
            if (intentOrigin.equals("Login")) {
                toast = Toast.makeText(getApplicationContext(), "Welcome back, " + currentUser.getUsername(), Toast.LENGTH_SHORT);
                toast.show();
            }
            loadUserData(currentUser);
        } else {
            toast = Toast.makeText(getApplicationContext(), "Error loading user data", Toast.LENGTH_SHORT);
            toast.show();
        }

        btnAddEntry.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            intent.putExtra("currentUser", (Serializable) currentUser);
            intent.putExtra("origin", "Main");
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Loads user weight records from DB and populates the table
     *
     * @param user  the currently logged in user
     */
    public void loadUserData(AuthenticatedUser user) {

        // create a db object to fetch the data
        DBHandler db = new DBHandler(this);

        List<WeightRecord> records = db.getWeightRecords(user);

        // clear the default table rows
        int viewCount = sv.getChildCount();
        sv.removeViews(0, viewCount);
        int count = 0;
        // loop through fetched records and generate a row for each
        for (WeightRecord wr:records) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // use a different bg color for even/odd rows
            tr.setBackgroundColor(Color.parseColor(count % 2 == 0 ? "#DAE8FC" : "#DDDDC2"));
            tr.setPadding(10,20,10,8);

            TextView txt1 = new TextView(this);
            TextView txt2 = new TextView(this);

            txt1.setTextSize(20);
            txt1.setLayoutParams(new TableRow.LayoutParams(220, 100, 1.4f));
            txt1.setPadding(20,6,20,6);

            txt2.setTextSize(20);
            txt2.setLayoutParams(new TableRow.LayoutParams(180, 100, 1.2f));
            txt2.setPadding(20,6,20,6);

            // assign values to text views in table row
            txt1.setText(String.valueOf(wr.getDate()));
            txt2.setText(String.valueOf(wr.getWeight()));

            // build the edit and delete buttons and add to the row
            ImageButton ibEdit = new ImageButton(this);
            ImageButton ibDelete = new ImageButton(this);

            TableRow.LayoutParams btnParams = new TableRow.LayoutParams(40, 80, 0.70f);
            btnParams.setMargins(4, 0, 4, 0);

            ibEdit.setLayoutParams(btnParams);
            ibEdit.setImageResource(R.drawable.ic_menu_edit);
            ibEdit.setBackgroundColor(getResources().getColor(R.color.edit));
            ibEdit.setBackgroundResource(R.drawable.border_button);

            ibDelete.setLayoutParams(btnParams);
            ibDelete.setImageResource(R.drawable.ic_menu_delete);
            ibDelete.setBackgroundColor(getResources().getColor(R.color.delete));

            // add edit button
            ibEdit.setOnClickListener(l -> {
                Intent intent = new Intent(MainActivity.this, EditEntryActivity.class);
                intent.putExtra("currentUser", (Serializable) currentUser);
                intent.putExtra("editWeightRecord", (Serializable) wr);
                intent.putExtra("origin", "Main");
                startActivity(intent);
            });
            // add a delete button to the last column
            ibDelete.setOnClickListener(l -> {
                if (db.deleteWeightRecord(wr.getId())) {
                    tr.removeView(ibDelete);
                    loadUserData(currentUser);
                }
            });

            tr.addView(txt1);
            tr.addView(txt2);
            tr.addView(ibEdit);
            tr.addView(ibDelete);
            // add the new row to the table
            sv.addView(tr);

            count ++;
        }

    }
}