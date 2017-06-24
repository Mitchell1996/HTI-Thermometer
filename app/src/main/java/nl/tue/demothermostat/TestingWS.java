/**
 * Class for testing Web Service (http://wwwis.win.tue.nl/2id40-ws),
 * gives a few examples of
 * getting data from and uploading data to the server
 */
package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;
import org.w3c.dom.Text;

import java.net.ConnectException;

public class TestingWS extends Activity {

    Button getdata, putdata;
    TextView data1, data2, data3, data4, data5, data6, data7;
    String oldv, newv, date, time, dayt, nightt, cnt, tgt, wpg;

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

        /* When the user clicks on GET Data button the value of the corresponding parameter is read from the server
        and displayed in TextView data1
         */
        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getData();
                setData();
            }
        });

        /* When the user clicks on PUT Data button the old value of the corresponding parameter is read from the server
        and displayed in TextView data1, the new uploaded value is displayed in TextView data2
         */
        putdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Toast toast = Toast.makeText(getApplicationContext(), "Target Temperature uploaded!", Toast.LENGTH_SHORT);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            oldv = HeatingSystem.get("targetTemperature");
                            HeatingSystem.put("targetTemperature", String.valueOf(ThermostatActivity.temp_target));     //store local temp_target to server
                            newv = HeatingSystem.get("targetTemperature");

                            /* Uncomment the following parts to see how to work with the properties of the week program */
                            // Get the week program
                            WeekProgram wpg = HeatingSystem.getWeekProgram();
                            // Set the week program to default
                            wpg.setDefault();
                            /*
                            wpg.data.get("Monday").set(5, new Switch("day", true, "07:30"));
                            wpg.data.get("Monday").set(1, new Switch("night", true, "08:30"));
                            wpg.data.get("Monday").set(6, new Switch("day", true, "18:00"));
                            wpg.data.get("Monday").set(7, new Switch("day", true, "12:00"));
                            wpg.data.get("Monday").set(8, new Switch("day", true, "18:00"));
                            boolean duplicates = wpg.duplicates(wpg.data.get("Monday"));
                            System.out.println("Duplicates found "+duplicates);
                            */
                            //Upload the updated program
                            //HeatingSystem.setWeekProgram(wpg);

                            data1.post(new Runnable() {
                                @Override
                                public void run() {
                                    //data1.setText(oldv);
                                }
                            });
                            data2.post(new Runnable() {
                                @Override
                                public void run() {
                                    //data2.setText(newv);
                                }
                            });
                            toast.show();
                        } catch (Exception e) {
                            System.err.println("Error from getdata " + e);
                        }
                    }
                }).start();

            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationBar);
        bottomNavigationView.setSelectedItemId(R.id.Test);


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
                    wpg = HeatingSystem.get("weekProgramState");
                } catch (ConnectException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void setData() {
        data1.setText(date);
        data2.setText(time);
        data3.setText(dayt);
        data4.setText(nightt);
        data5.setText(cnt);
        data6.setText(tgt);
        data7.setText(wpg);
    }
}
