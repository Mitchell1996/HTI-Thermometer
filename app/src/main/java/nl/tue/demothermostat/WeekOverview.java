package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Created by nstash on 06/05/15.
 */
public class WeekOverview extends AppCompatActivity {
    public WeekProgram wpg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        Button monday = (Button) findViewById(R.id.Monday);
        Button tuesday = (Button) findViewById(R.id.Tuesday);
        Button wednesday = (Button) findViewById(R.id.Wednesday);
        Button thursday = (Button) findViewById(R.id.Thursday);
        Button friday = (Button) findViewById(R.id.Friday);
        Button saturday = (Button) findViewById(R.id.Saturday);
        Button sunday = (Button) findViewById(R.id.Sunday);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpg = HeatingSystem.getWeekProgram();
                    return;
                } catch (ConnectException e) {
                    e.printStackTrace();
                } catch (CorruptWeekProgramException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MondaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TuesdaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WednesdaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ThursdaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FridaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SaturdaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SundaySwitches.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationBar);
        bottomNavigationView.setSelectedItemId(R.id.WeekOverview);


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