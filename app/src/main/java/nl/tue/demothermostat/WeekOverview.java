package nl.tue.demothermostat;

import android.app.Activity;
import android.os.Bundle;

import org.thermostatapp.util.WeekProgram;

/**
 * Created by nstash on 06/05/15.
 */
public class WeekOverview extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        WeekProgram weekProgram =  new WeekProgram();
        weekProgram.setDefault();
    }
}