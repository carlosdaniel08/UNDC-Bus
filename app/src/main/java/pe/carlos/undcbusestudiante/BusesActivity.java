package pe.carlos.undcbusestudiante;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class BusesActivity extends AppCompatActivity {

    ImageButton BusUno, BusDos, BusTres, BusCuatro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        BusUno = findViewById(R.id.BusUno);

        BusUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusesActivity.this, TicketActivity.class);
                startActivity(intent);

            }
        });

        BusDos = findViewById(R.id.BusDos);
        BusTres = findViewById(R.id.BusTres);
        BusCuatro = findViewById(R.id.BusCuatro);


    }
}