package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.InvalidInputValueException;

import java.net.ConnectException;

public class ThermostatActivity extends Activity {

    public static double temp, temp_current, temp_target, temp_day, temp_night;             //this should be retrieved from the server to show the current temp
    TextView currentTemp, targetTemp;
    boolean firstPull = false;
    boolean vacationMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.Parent);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        Button bPlus = (Button) findViewById(R.id.bPlus);
        Button bMinus = (Button) findViewById(R.id.bMinus);
        final ToggleButton targetToggle = (ToggleButton) findViewById(R.id.targetToggle);
        final ToggleButton vacationToggle = (ToggleButton) findViewById(R.id.vacationToggle);
        final Switch dNSwitch = (Switch) findViewById(R.id.switch1);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationBar);

        /* Constantly pulls temperatures from server and displays
        the current temperature only
         */
        final Thread tempsPull = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        temp_current = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                        if (!firstPull) {       //only retrieve these temps once at start
                            vacationMode = HeatingSystem.get("weekProgramState").equals("off");
                            temp_target = Double.parseDouble(HeatingSystem.get("targetTemperature"));
                            temp_day = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                            temp_night = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                            firstPull = true;
                            temp = temp_target;
                        }
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    }
                    currentTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            currentTemp.setText(String.valueOf(temp_current));
                            targetTemp.setText(String.valueOf(temp) + " \u2103");
                            vacationToggle.setChecked(vacationMode);
                        }
                    });
                }
            }
        });

        tempsPull.start();      //start the thread

        dNSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (targetToggle.isChecked()) {     //only if the ToggleButton is checked
                    if (isChecked) {        //night temperature
                        ll.setBackgroundColor(getColor(R.color.night));     //toggle is enabled
                        temp = temp_night;
                    } else {        //day temperature
                        ll.setBackgroundColor(getColor(R.color.day));      //toggle is disabled
                        temp = temp_day;
                    }
                    targetTemp.setText(temp + " \u2103");
                }
            }
        });
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp <= 29) {       //[5.0, 29.0]
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {     //night
                            temp_night++;
                            temp = temp_night;
                        } else {        //day
                            temp_day++;
                            temp = temp_day;
                        }
                    } else {        //target
                        temp_target++;
                        temp = temp_target;
                    }
                } else if (temp < 30) {     //[29.1, 29.9]
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {     //night
                            temp_night = 30;
                            temp = temp_night;
                        } else {        //day
                            temp_day = 30;
                            temp = temp_day;
                        }
                    } else {        //target
                        temp_target = 30;
                        temp = temp_target;
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("dayTemperature", String.valueOf(temp_day));
                            HeatingSystem.put("nightTemperature", String.valueOf(temp_night));
                            HeatingSystem.put("targetTemperature", String.valueOf(temp_target));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp >= 6) {        //[6.0, 30.0]
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {     //night
                            temp_night--;
                            temp = temp_night;
                        } else {        //day
                            temp_day--;
                            temp = temp_day;
                        }
                    } else {
                        temp_target--;
                        temp = temp_target;
                    }
                } else if (temp > 5) {      //[5.0, 5,9]
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {     //night
                            temp_night = 5;
                            temp = temp_night;
                        } else {        //day
                            temp_day = 5;
                            temp = temp_day;
                        }
                    } else {        //target
                        temp_target = 5;
                        temp = temp_target;
                    }
                } else {        //[5.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("dayTemperature", String.valueOf(temp_day));
                            HeatingSystem.put("nightTemperature", String.valueOf(temp_night));
                            HeatingSystem.put("targetTemperature", String.valueOf(temp_target));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        targetToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* On toggle it will display the target/day/night temperature
                 */
                if (targetToggle.isChecked()) {     //if toggle on
                    if (dNSwitch.isChecked()) {     //if night
                        temp = temp_night;
                    } else {        //if day
                        temp = temp_day;
                    }
                } else {        //if toggle off
                    temp = temp_target;
                }
                targetTemp.setText(String.valueOf(temp) + "\u2103");        //update the TextView
            }
        });

        vacationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vacationMode = !vacationMode;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (vacationMode) {
                                HeatingSystem.put("weekProgramState", "off");       //VacationMode on
                            } else {
                                HeatingSystem.put("weekProgramState", "on");        //VacationMode off
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.Thermostat:
                                break;
                            case R.id.WeekOverview:
                                Intent intent2 = new Intent(getBaseContext(), WeekOverview.class);
                                startActivity(intent2);
                                break;
                            case R.id.Test:
                                Intent intent3 = new Intent(getBaseContext(), TestingWS.class);
                                startActivity(intent3);
                                break;
                        }
                        return false;
                    }
                });
    }
}

