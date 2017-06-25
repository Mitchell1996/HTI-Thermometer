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
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.Arrays;

public class MondaySwitches extends AppCompatActivity {
    String day = "Monday";
    Switch[] switches = new Switch[9];
    final String[] type = new String[1];
    String[] stuff = {"00:00", "12:00"};

    final String[] items = new String[11];
    final String[] times = new String[11];
    final String[] onoff = new String[11];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mondayactivity);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/48";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fAB = (FloatingActionButton)
                findViewById(R.id.floatingAddButton);

        Arrays.fill(items, "empty");


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

        final Toast toast = Toast.makeText(getApplicationContext(), "end of try/CATCH", Toast.LENGTH_SHORT);
        final Toast toast1 = Toast.makeText(getApplicationContext(), "end of TRY/catch", Toast.LENGTH_SHORT);
        final String[] error = new String[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    toast1.show();
                    final WeekProgram wpg = HeatingSystem.getWeekProgram();
                    //wpg.setDefault();
                    for (int i = 0; i < 10; i++) {
                        switches[i] = wpg.data.get("Monday").get(i);
                    }
                    for (int i = 0; i < 10; i++) {
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
