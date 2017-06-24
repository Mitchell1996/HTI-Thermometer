package nl.tue.demothermostat;

<<<<<<< HEAD
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.View;
=======
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
>>>>>>> 51017cc30bf40a392cc112ac553b30674f12bbb1

        import org.thermostatapp.util.HeatingSystem;
        import org.thermostatapp.util.WeekProgram;

public class MondaySwitches extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mondayactivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeekOverview.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fAB = (FloatingActionButton)
                findViewById(R.id.FloatingAddButton);
        fAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddSwitch.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_up, R.anim.exit);
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WeekProgram wpg = HeatingSystem.getWeekProgram();
                    for (int i = 0; i < 10; i++) {
                        wpg.data.get("Monday").get(i);
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

    }
}
