package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ThermostatActivity extends Activity {

    public static double temp_current;             //this should be retrieved from the server to show the current temp
    public static double temp_target = 21.0;       //is accessible from other classes
    TextView currentTemp, targetTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        final LinearLayout ll = (LinearLayout)findViewById(R.id.Parent);
        ll.setBackgroundColor(Color.BLUE);      //starts with BLUE by default

        currentTemp = (TextView)findViewById(R.id.currentTemp);
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        Button bPlus = (Button) findViewById(R.id.bPlus);
        Button bMinus = (Button)findViewById(R.id.bMinus);

        //Sets TextView variables to their corresponding UI element by its id
        currentTemp = (TextView)findViewById(R.id.currentTemp);     //TBD TextView for current temp
        targetTemp = (TextView)findViewById(R.id.targetTemp);       //TBD TextView for target temp

        Button weekOverview = (Button)findViewById(R.id.week_overview);

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

        Switch dNSwitch = (Switch)findViewById(R.id.switch1);

        dNSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll.setBackgroundColor(Color.BLACK);     //toggle is enabled
                } else {
                    ll.setBackgroundColor(Color.BLUE);      //toggle is disabled
                }
            }
        });
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp_target < 30) {
                    temp_target++;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature above 30", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp_target + " \u2103");
            }
        });
        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp_target > 5) {
                    temp_target--;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You can't set the Target Temperature below 5", Toast.LENGTH_SHORT);
                    toast.show();
                }
                targetTemp.setText(temp_target + " \u2103");
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