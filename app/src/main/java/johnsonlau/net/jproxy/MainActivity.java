package johnsonlau.net.jproxy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view){
        Toast.makeText(MainActivity.this, view.getId()+"", Toast.LENGTH_LONG).show();
        new ProxyThread().start();
    }
}
