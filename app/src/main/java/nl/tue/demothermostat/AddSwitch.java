package nl.tue.demothermostat;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;


public class AddSwitch extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_switch);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.cancelSaveBar);
    }
}
