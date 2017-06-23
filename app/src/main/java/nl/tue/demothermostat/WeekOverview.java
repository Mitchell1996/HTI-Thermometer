package nl.tue.demothermostat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.thermostatapp.util.WeekProgram;

/**
 * Created by nstash on 06/05/15.
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        Button monday = (Button)findViewById(R.id.Monday);
        Button tuesday = (Button)findViewById(R.id.Tuesday);
        Button wednesday = (Button)findViewById(R.id.Wednesday);
        Button thursday = (Button)findViewById(R.id.Thursday);
        Button friday = (Button)findViewById(R.id.Friday);
        Button saturday = (Button)findViewById(R.id.Saturday);
        Button sunday = (Button)findViewById(R.id.Sunday);

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

            }
        });


    }
}