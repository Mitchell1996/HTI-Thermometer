package nl.tue.demothermostat;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;


public class AddSwitch extends AppCompatActivity {
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_switch);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String day = extras.getString("day");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekOverview.class);
                startActivity(intent);
            }
        });

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        final Switch switchSwitch = (Switch) findViewById(R.id.switchSwitch);
        switchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = "night";
                } else {
                    type = "day";
                }
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.cancelSaveBar);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = new Intent(getBaseContext(), MondaySwitches.class);
                        switch (item.getItemId()) {
                            case R.id.Cancel:
                                startActivity(intent);
                                break;
                            case R.id.Save:
                                String hourString;
                                if (timePicker.getHour() < 10)
                                    hourString = "0" + timePicker.getHour();
                                else
                                    hourString = "" + timePicker.getHour();

                                String minuteString;
                                if (timePicker.getMinute() < 10)
                                    minuteString = "0" + timePicker.getMinute();
                                else
                                    minuteString = "" + timePicker.getMinute();

                                String time = hourString + ":" + minuteString;
                                Toast.makeText(getBaseContext(), time, Toast.LENGTH_SHORT).show();
                                intent.putExtra("type", type);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
    }
}
