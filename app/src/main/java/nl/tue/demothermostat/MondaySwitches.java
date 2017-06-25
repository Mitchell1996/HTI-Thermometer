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
    ArrayList<Switch> switches = new ArrayList<>();
    static WeekProgram wpg;
    String day = "Monday";
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mondayactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fAB = (FloatingActionButton)
                findViewById(R.id.floatingAddButton);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpg = HeatingSystem.getWeekProgram();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        for (int i = 0; i < 10; i++) {
            if (wpg.data.get(day).get(i).getState()) {       //if switch is on
                items[i] = wpg.data.get(day).get(i).getType(); //crash?!
            }
        }


        populateListView();

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
    }

    private void populateListView() {
        String[] myItems = {};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getBaseContext(), R.layout.switch_temperature, items);

        ListView list = (ListView) findViewById(R.id.SwitchList);
        list.setAdapter(adapter);
    }
}
