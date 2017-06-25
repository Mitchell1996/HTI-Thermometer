package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;

public class MondaySwitches extends AppCompatActivity {
    String day = "Monday";
    Switch[] switches = new Switch[9];
    final String[] type = new String[1];
    String[] stuff = {"00:00", "12:00"};
    String[] end;

    final String[] items = new String[11];
    final String[] times = new String[11];
    final String[] onoff = new String[11];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mondayactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fAB = (FloatingActionButton)
                findViewById(R.id.floatingAddButton);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekOverview.class);
                startActivity(intent);
            }
        });

        fAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddSwitch.class);
                intent.putExtra("day", day);        //to pass on the day
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final WeekProgram wpg = HeatingSystem.getWeekProgram();
                    wpg.setDefault();
                    //wpg.data.get("Monday").set(1, new Switch("night", true, "08:30"));

                    for (int i=0; i<10; i++) {
                        switches[i] = wpg.data.get(day).get(i);
                    }
                    for (int i = 0; i<10; i++) {
                        items[i] = switches[i].getType();
                        times[i] = switches[i].getTime();
                        onoff[i] = String.valueOf(switches[i].getState());
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
        populateListView();

    }



    private void populateListView() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getBaseContext(), R.layout.switch_temperature, items);

        ListView list = (ListView) findViewById(R.id.SwitchList);
        list.setAdapter(adapter);
    }
}
