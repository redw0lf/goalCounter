package net.scripterscorner.goalCounterSensorMeasuring;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class GoalCounterSensorMeasuring extends Activity   {




    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



        final Button button = (Button) findViewById(R.id.buttonNewGame);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Game.class);
                startActivity(intent);
            }
        });
    }


}
