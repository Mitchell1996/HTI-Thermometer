package nl.tue.demothermostat;

import android.app.Activity;
import android.app.DownloadManager;
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

import java.math.RoundingMode;
import java.net.ConnectException;
import java.text.DecimalFormat;



public class ThermostatActivity extends Activity {

    public static double temp, temp_current, temp_target, temp_day, temp_night;             //this should be retrieved from the server to show the current temp
    TextView currentTemp, targetTemp, dNTemp;
    boolean firstPull = false;
    boolean vacationMode;
    SeekBar dNSlider, tSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.Parent);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        dNTemp = (TextView) findViewById(R.id.dNTemp);
        dNSlider = (SeekBar) findViewById(R.id.dNSlider);
        tSlider = (SeekBar) findViewById(R.id.tSlider);
        Button bPlus = (Button) findViewById(R.id.bPlus);       //for targetTemp
        Button bMinus = (Button) findViewById(R.id.bMinus);     //for targetTemp
        Button bPlus2 = (Button) findViewById(R.id.bPlus2);     //for day/night temp
        Button bMinus2 = (Button) findViewById(R.id.bMinus2);   //for day/night temp
        final ToggleButton vacationToggle = (ToggleButton) findViewById(R.id.vacationToggle);
        final ToggleButton dNToggle = (ToggleButton) findViewById(R.id.DayNightToggle);
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
                                int n = (int) temp_target - 5;
                                tSlider.setProgress(n);
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

        final DecimalFormat df = new DecimalFormat("##.#");
        df.setRoundingMode(RoundingMode.CEILING);

        tempsPull.start();      //start the thread

        dNToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {        //night temperature
                    temp = temp_night;
                } else {        //day temperature
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
                if (temp_target <= 29.9) {       //[5.0, 29.9]
                    temp_target += 0.1;
                } else {        //[30.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                temp_target = Double.valueOf(df.format(temp_target));
                targetTemp.setText(temp_target + " \u2103");
                int n = (int) temp_target - 5;
                tSlider.setProgress(n);

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
                if (temp_target >= 5.1) {        //[5.1, 30.0]
                    temp_target -= 0.1;
                } else {        //[5.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                temp_target = Double.valueOf(df.format(temp_target));
                targetTemp.setText(temp_target + " \u2103");
                int n = (int) temp_target - 5;
                tSlider.setProgress(n);

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
                if (temp <= 29.9) {       //[5.0, 29.9]
                    if (dNToggle.isChecked()) {     //night
                        temp_night += 0.1;
                        temp = temp_night;
                    } else {        //day
                        temp_day += 0.1;
                        temp = temp_day;
                    }
                } else {        //[30.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                int i = (int) temp - 5;
                dNSlider.setProgress(i);
                temp = Double.valueOf(df.format(temp));
                temp_day = Double.valueOf(df.format(temp_day));
                temp_night = Double.valueOf(df.format(temp_night));
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
                if (temp >= 5.1) {       //[5.1 30.0]
                    if (dNToggle.isChecked()) {     //night
                        temp_night -= 0.1;
                        temp = temp_night;
                    } else {        //day
                        temp_day -= 0.1;
                        temp = temp_day;
                    }
                } else {        //[5.0]
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                int i = (int) temp - 5;
                dNSlider.setProgress(i);
                temp = Double.valueOf(df.format(temp));
                temp_day = Double.valueOf(df.format(temp_day));
                temp_night = Double.valueOf(df.format(temp_night));
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
        tSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp_target = progress + 5;
                targetTemp.setText(temp_target + "\u2103");
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
                            HeatingSystem.put("targetTemperature", String.valueOf(temp_target));
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
                if (dNToggle.isChecked()) {     //night
                    temp_night = temp;
                } else {        //day
                    temp_day = temp;
                }
                temp_day = Double.valueOf(df.format(temp_day));
                temp_night = Double.valueOf(df.format(temp_night));
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
}

