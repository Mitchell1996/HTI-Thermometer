package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.Arrays;

public class FridaySwitches extends AppCompatActivity {
    String day = "Friday";
    Switch[] switches = new Switch[9];
    final String[] type = new String[1];
    String[] stuff = {"00:00", "12:00"};
    final String[] items = new String[9];
    final String[] times = new String[9];
    final String[] onoff = new String[9];
    final String[] whole = new String[9];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridayactivity);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/48";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fAB = (FloatingActionButton)
                findViewById(R.id.floatingAddButton);

        Arrays.fill(items, "empty");
        Arrays.fill(times, "1");
        Arrays.fill(onoff, "1");
        Arrays.fill(whole, "0");


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
                    for (int i = 0; i < 9; i++) {
                        switches[i] = wpg.data.get("Monday").get(i);
                    }
                    for (int i = 0; i < 9; i++) {
                        times[i] = switches[i].getTime();
                        items[i] = switches[i].getType();
                        onoff[i] = String.valueOf(switches[i].getState());
                        whole[i] = "Type: " + items[i] + ", Time: " + times[i] + ", On: "  + onoff[i];
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
                new ArrayAdapter<String>(getBaseContext(), R.layout.switch_temperature, whole);

        ListView list = (ListView) findViewById(R.id.SwitchList);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                
                return true;
            }
        });
    }
}
