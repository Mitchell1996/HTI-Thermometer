package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.thermostatapp.util.HeatingSystem;

import java.net.ConnectException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class ThermostatActivity extends Activity {

    public static double temp, temp_current, temp_target, temp_day, temp_night;             //this should be retrieved from the server to show the current temp
    TextView currentTemp, targetTemp;
    private Handler yourHandler = new Handler();
    private Timer yourTimer = null;
    boolean firstPull = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.Parent);

        currentTemp = (TextView) findViewById(R.id.currentTemp);
        targetTemp = (TextView) findViewById(R.id.targetTemp);
        Button bPlus = (Button) findViewById(R.id.bPlus);
        Button bMinus = (Button) findViewById(R.id.bMinus);
        Button weekOverview = (Button) findViewById(R.id.week_overview);
        final ToggleButton targetToggle = (ToggleButton) findViewById(R.id.targetToggle);

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
                        }
                    });
                }
            }
        });

        tempsPull.start();
        weekOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekOverview.class);
                startActivity(intent);
            }
        });

        Button testingWS = (Button) findViewById(R.id.testing_ws);

        testingWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TestingWS.class);
                startActivity(intent);
            }
        });

        final Switch dNSwitch = (Switch) findViewById(R.id.switch1);

        dNSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll.setBackgroundColor(getColor(R.color.night));     //toggle is enabled
                } else {
                    ll.setBackgroundColor(getColor(R.color.day));      //toggle is disabled
                }
            }
        });
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp_target < 30) {
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {
                            temp_night++;
                            temp = temp_night;
                        } else {
                            temp_day++;
                            temp = temp_day;
                        }
                    } else {
                        temp_target++;
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
                if (temp_target > 5) {
                    if (targetToggle.isChecked()) {
                        if (dNSwitch.isChecked()) {
                            temp_night--;
                            temp = temp_night;
                        } else {
                            temp_day--;
                            temp = temp_day;
                        }
                    } else {
                        temp_target--;
                        temp = temp_target;
                    }
                } else {
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
                /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (targetToggle.isChecked()) {
                                if (dNSwitch.isChecked()) {

                                }
                            }
                            temp_target = Double.parseDouble(HeatingSystem.get("targetTemperature"));
                            temp_day = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                            temp_night = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        targetTemp.post(new Runnable() {
                            @Override
                            public void run() {
                                targetTemp.setText(String.valueOf(temp) + " \u2103");
                            }
                        });
                    }
                }).start(); */
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

