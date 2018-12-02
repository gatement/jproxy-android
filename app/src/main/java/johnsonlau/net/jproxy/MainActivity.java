package johnsonlau.net.jproxy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.johnsonlau.jproxy.lib.ProxyMain;
import johnsonlau.net.jproxy.conf.MyProxyLog;
import johnsonlau.net.jproxy.conf.MyProxySettings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view){
        Toast.makeText(MainActivity.this, "Starting HTTP Proxy Server", Toast.LENGTH_LONG).show();
        new ProxyMain(new MyProxySettings(), new MyProxyLog()).run();
    }
}
