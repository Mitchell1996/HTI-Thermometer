package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.SeekBar;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.InvalidInputValueException;

import java.net.ConnectException;

public class ThermostatActivity extends Activity {

    public static double temp, temp_current, temp_target, temp_day, temp_night;             //this should be retrieved from the server to show the current temp
    TextView currentTemp, targetTemp, dNTemp;
    boolean firstPull = false;
    boolean vacationMode;
    SeekBar dNSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.Parent);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        dNTemp = (TextView) findViewById(R.id.dNTemp);
        dNSlider = (SeekBar) findViewById(R.id.dNSlider);
        Button bPlus = (Button) findViewById(R.id.bPlus);       //for targetTemp
        Button bMinus = (Button) findViewById(R.id.bMinus);     //for targetTemp
        Button bPlus2 = (Button) findViewById(R.id.bPlus2);     //for day/night temp
        Button bMinus2 = (Button) findViewById(R.id.bMinus2);   //for day/night temp
        final ToggleButton vacationToggle = (ToggleButton) findViewById(R.id.vacationToggle);
        final Switch dNSwitch = (Switch) findViewById(R.id.switch1);        //day/night switch
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationBar);
        bottomNavigationView.setSelectedItemId(R.id.Thermostat);


        /* Constantly pulls temperatures from server and displays
        the current temperature only
         */
        Thread tempsPull = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
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
                            temp = temp_day;
                        }
                    } catch (ConnectException e) {
                        e.printStackTrace();
                    }
                    currentTemp.post(new Runnable() {
                        @Override
                        public void run() {
                            currentTemp.setText(String.valueOf(temp_current));
                            if (!firstPull) {
                                targetTemp.setText(String.valueOf(temp_target) + " \u2103");
                                dNTemp.setText(String.valueOf(temp) + " \u2103");
                                int i = (int) temp - 5;
                                dNSlider.setProgress(i);
                                firstPull = true;
                            }
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
                if (isChecked) {        //night temperature
                    //toggle is enabled
                    temp = temp_night;
                } else {        //day temperature
                    //toggle is disabled
                    temp = temp_day;
                }
                int i = (int) temp - 5;
                dNSlider.setProgress(i);
                dNTemp.setText(temp + " \u2103");
            }
        });
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp_target <= 29) {       //[5.0, 29.0]
                    temp_target++;
                } else if (temp_target < 30) {     //[29.1, 29.9]
                    temp_target = 30;
                } else {        //[30.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp_target + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
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
                if (temp_target >= 6) {        //[6.0, 30.0]
                    temp_target--;
                } else if (temp_target > 5) {      //[5.1, 5,9]
                    temp_target = 5;
                } else {        //[5.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp_target + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("targetTemperature", String.valueOf(temp_target));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        bPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp <= 29) {       //[5.0, 29.0]
                    if (dNSwitch.isChecked()) {     //night
                        temp_night++;
                        temp = temp_night;
                    } else {        //day
                        temp_day++;
                        temp = temp_day;
                    }
                } else if (temp < 30) {     //[29.1, 29.9]
                    if (dNSwitch.isChecked()) {     //night
                        temp_night = 30;
                        temp = temp_night;
                    } else {        //day
                        temp_day = 30;
                        temp = temp_day;
                    }
                } else {        //[30.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                int i = (int) temp - 5;
                dNSlider.setProgress(i);
                dNTemp.setText(temp + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("dayTemperature", String.valueOf(temp_day));
                            HeatingSystem.put("nightTemperature", String.valueOf(temp_night));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        bMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp >= 6) {       //[6.0, 30.0]
                    if (dNSwitch.isChecked()) {     //night
                        temp_night--;
                        temp = temp_night;
                    } else {        //day
                        temp_day--;
                        temp = temp_day;
                    }
                } else if (temp > 5) {     //[5.1, 5.9]
                    if (dNSwitch.isChecked()) {     //night
                        temp_night = 5;
                        temp = temp_night;
                    } else {        //day
                        temp_day = 5;
                        temp = temp_day;
                    }
                } else {        //[5.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                int i = (int) temp - 5;
                dNSlider.setProgress(i);
                dNTemp.setText(temp + " \u2103");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("dayTemperature", String.valueOf(temp_day));
                            HeatingSystem.put("nightTemperature", String.valueOf(temp_night));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
        dNSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                temp = i + 5;
                dNTemp.setText(temp + " \u2103");
                if (dNSwitch.isChecked()) {     //night
                    temp_night = temp;
                } else {        //day
                    temp_day = temp;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("dayTemperature", String.valueOf(temp_day));
                            HeatingSystem.put("nightTemperature", String.valueOf(temp_night));
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
                                overridePendingTransition(R.anim.enter_right, R.anim.exit);
                                break;
                            case R.id.Test:
                                Intent intent3 = new Intent(getBaseContext(), TestingWS.class);
                                startActivity(intent3);
                                overridePendingTransition(R.anim.enter_right, R.anim.exit);
                                break;
                        }
                        return false;
                    }
                });
    }

    /*
    private class getTemps extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                temp_current = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                if (!firstPull) {       //only retrieve these temps once at start
                    vacationMode = HeatingSystem.get("weekProgramState").equals("off");
                    temp_target = Double.parseDouble(HeatingSystem.get("targetTemperature"));
                    temp_day = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    temp_night = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    temp = temp_day;
                }
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            return null;
        }
    } */
}

