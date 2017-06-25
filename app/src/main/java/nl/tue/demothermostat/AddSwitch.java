package nl.tue.demothermostat;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.net.ConnectException;
import java.util.Arrays;


public class AddSwitch extends AppCompatActivity {
    String type = "day";
    String day = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_switch);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/48";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            day = extras.getString("day");
           // Log.d("Day?", day);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);


        ToggleButton toggleDN = (ToggleButton) findViewById(R.id.toggleDN);
        toggleDN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final WeekProgram wpg = HeatingSystem.getWeekProgram();
                    final Switch[] switches = new Switch[10];
                    for (int i = 0; i < 10; i++) {
                        switches[i] = wpg.data.get(day).get(i);
                    }
                } catch (CorruptWeekProgramException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = new Intent(getBaseContext(), MondaySwitches.class);
                        switch (item.getItemId()) {
                            case R.id.Cancel:
                                finish();
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

                                final String time = hourString + ":" + minuteString;
                                Toast.makeText(getBaseContext(), time, Toast.LENGTH_SHORT).show();

                                new Thread(new Runnable() {
                                    WeekProgram wpg;
                                    Switch[] switches = new Switch[10];

                                    final Toast toast = Toast.makeText(getApplicationContext(), "Target Temperature uploaded!", Toast.LENGTH_SHORT);

                                    @Override
                                    public void run() {
                                        try {
                                            wpg = HeatingSystem.getWeekProgram();
                                        } catch (ConnectException e) {
                                            e.printStackTrace();
                                        } catch (CorruptWeekProgramException e) {
                                            e.printStackTrace();
                                        }
                                        for (int i = 0; i < 10; i++) {
                                            switches[i] = wpg.data.get(day).get(i);
                                            if (!switches[i].getState()) {      //if disabled
                                                wpg.data.get(day).set(i, new Switch(type, true, time));

                                               // Log.d("all things", type + "  : " + time);
                                                toast.show();

                                                HeatingSystem.setWeekProgram(wpg);
                                                i = 10;
                                            }
                                        }
                                    }
                                }).start();
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
    }
}
