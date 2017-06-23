package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.thermostatapp.util.HeatingSystem;

import java.net.ConnectException;

public class ThermostatActivity extends Activity {

    public static double temp, temp_current, temp_target, temp_day, temp_night;             //this should be retrieved from the server to show the current temp
    TextView currentTemp, targetTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        final LinearLayout ll = (LinearLayout)findViewById(R.id.Parent);

        //Gets day/night temperatures from server and stores them in the variables
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    temp_day = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    temp_night = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    temp_current = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                    temp_target = Double.parseDouble(HeatingSystem.get("targetTemperature"));
                    currentTemp.setText(String.valueOf(temp_current));
                    targetTemp.setText(String.valueOf(temp_target));
                } catch (ConnectException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        currentTemp = (TextView)findViewById(R.id.currentTemp);
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        Button bPlus = (Button) findViewById(R.id.bPlus);
        Button bMinus = (Button)findViewById(R.id.bMinus);
        Button weekOverview = (Button)findViewById(R.id.week_overview);
        final ToggleButton targetToggle = (ToggleButton) findViewById(R.id.targetToggle);

        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentTemp.setText(String.valueOf(temp_current));
            }
        }); */

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentTemp.setText(String.valueOf(temp_current));
                            }
                        });
                    } catch (InterruptedException e) {
                        // ooops
                    }
                }
            }
        }).start();

        weekOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekOverview.class);
                startActivity(intent);
            }
        });

        Button testingWS = (Button)findViewById(R.id.testing_ws);

        testingWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TestingWS.class);
                startActivity(intent);
            }
        });

        final Switch dNSwitch = (Switch)findViewById(R.id.switch1);

        dNSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
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
