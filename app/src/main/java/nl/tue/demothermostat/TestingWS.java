/**
 * Class for testing Web Service (http://wwwis.win.tue.nl/2id40-ws),
 * gives a few examples of
 * getting data from and uploading data to the server
 */
package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;

import java.net.ConnectException;

public class TestingWS extends Activity {

    Button getdata, putdata;
    TextView data1, data2, data3, data4, data5, data6, data7, wpg1, wpg2, wpg3;
    String date, time, dayt, nightt, cnt, tgt, wpgState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_ws);

        /* Use BASE_ADDRESS dedicated for your group,
         * change 100 to you group number
		 */
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/48";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        getdata = (Button) findViewById(R.id.getdata);
        putdata = (Button) findViewById(R.id.putdata);
        data1 = (TextView) findViewById(R.id.textView11);
        data2 = (TextView) findViewById(R.id.textView12);
        data3 = (TextView) findViewById(R.id.textView13);
        data4 = (TextView) findViewById(R.id.textView14);
        data5 = (TextView) findViewById(R.id.textView15);
        data6 = (TextView) findViewById(R.id.textView16);
        data7 = (TextView) findViewById(R.id.textView17);
        wpg1 = (TextView) findViewById(R.id.wpg1);
        wpg2 = (TextView) findViewById(R.id.wpg2);
        wpg3 = (TextView) findViewById(R.id.wpg3);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationBar);
        bottomNavigationView.setSelectedItemId(R.id.Test);




        /* When the user clicks on GET Data button the value of the corresponding parameter is read from the server
        and displayed in TextView data1
         */
        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getData();
            }
        });

        /* When the user clicks on PUT Data button the old value of the corresponding parameter is read from the server
        and displayed in TextView data1, the new uploaded value is displayed in TextView data2
         */
        putdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Switch[] mon1 = new Switch[11];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final WeekProgram wpg = HeatingSystem.getWeekProgram();
                            final String[] items = new String[11];
                            final String[] times = new String[11];
                            final String[] onoff = new String[11];

                            for (int i=0; i<10; i++) {
                                mon1[i] = wpg.data.get("Monday").get(i);
                            }
                            for (int i = 0; i<10; i++) {
                                items[i] = mon1[i].getType();
                                times[i] = mon1[i].getTime();
                                onoff[i] = String.valueOf(mon1[i].getState());
                            }
                            wpg1.post(new Runnable() {
                                @Override
                                public void run() {
                                    wpg1.setText(items[0]+items[1]+items[2]+items[3]+items[4]+items[5]+items[6]+items[7]+items[8]+items[9]);
                                    wpg2.setText(times[0]+times[1]+times[2]+times[3]+times[4]+times[5]+times[6]+times[7]+times[8]+times[9]);
                                    wpg3.setText(onoff[0]+onoff[1]+onoff[2]+onoff[3]+onoff[4]+onoff[5]+onoff[6]+onoff[7]+onoff[8]+onoff[9]);
                                }
                            });
                        } catch (Exception e) {
                            System.err.println("Error from getdata " + e);
                            final Toast toast = Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT);
                            toast.show();
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
                                Intent intent1 = new Intent(getBaseContext(), ThermostatActivity.class);
                                startActivity(intent1);
                                overridePendingTransition(R.anim.enter_left, R.anim.exit);
                                break;
                            case R.id.WeekOverview:
                                Intent intent2 = new Intent(getBaseContext(), WeekOverview.class);
                                startActivity(intent2);
                                overridePendingTransition(R.anim.enter_left, R.anim.exit);
                                break;
                            case R.id.Test:

                                break;
                        }
                        return false;
                    }
                });
    }

    /* Pull data from the server and store them in local variables
     */
    void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cnt = HeatingSystem.get("currentTemperature");
                    date = HeatingSystem.get("day");
                    time = HeatingSystem.get("time");
                    tgt = HeatingSystem.get("targetTemperature");
                    dayt = HeatingSystem.get("dayTemperature");
                    nightt = HeatingSystem.get("nightTemperature");
                    wpgState = HeatingSystem.get("weekProgramState");
                } catch (ConnectException e) {
                    e.printStackTrace();
                }
                data1.post(new Runnable() {
                    @Override
                    public void run() {
                        data1.setText(date);
                        data2.setText(time);
                        data3.setText(dayt);
                        data4.setText(nightt);
                        data5.setText(cnt);
                        data6.setText(tgt);
                        data7.setText(wpgState);
                    }
                });
            }
        }).start();
    }
}
