package johnsonlau.net.jproxy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class MainActivity extends AppCompatActivity {

    EditText profileName;
    EditText serverAddr;
    EditText serverPort;
    EditText username;
    EditText password;
    EditText proxyPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileName = findViewById(R.id.profileName);
        serverAddr = findViewById(R.id.serverAddr);
        serverPort = findViewById(R.id.serverPort);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        proxyPort = findViewById(R.id.proxyPort);

        SharedPreferences namePrefs = getSharedPreferences(Prefs.PROFILE_PREFS_NAME, MODE_PRIVATE);
        String profileNameStr = namePrefs.getString(Prefs.PROFILE_PROFILE_NAME, "default");
        profileName.setText(profileNameStr);

        SharedPreferences prefs = getSharedPreferences(profileNameStr, MODE_PRIVATE);
        serverAddr.setText(prefs.getString(Prefs.SERVER_ADRR, ""));
        serverPort.setText(String.valueOf(prefs.getInt(Prefs.SERVER_PORT, 22)));
        username.setText(prefs.getString(Prefs.USERNAME, "root"));
        password.setText(prefs.getString(Prefs.PASSWORD, ""));
        proxyPort.setText(String.valueOf(prefs.getInt(Prefs.PROXY_PORT, 8119)));
    }

    public void onStart(View view){
        String serverAddrStr = serverAddr.getText().toString();
        int serverPortNum = 22;
        try {
            serverPortNum = Integer.parseInt(serverPort.getText().toString());
        } catch (NumberFormatException e) {
        }
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        int proxyPortNum = 8119;
        try {
            proxyPortNum = Integer.parseInt(proxyPort.getText().toString());
        } catch (NumberFormatException e) {
        }

        Toast.makeText(MainActivity.this, "Starting HTTP Proxy Server", Toast.LENGTH_LONG).show();
        ProxyService.start(this, serverAddrStr, serverPortNum, usernameStr, passwordStr, proxyPortNum);
    }

    public void onLoadProfile(View view){
        String profileNameStr = profileName.getText().toString();
        SharedPreferences prefs = getSharedPreferences(profileNameStr, MODE_PRIVATE);

        serverAddr.setText(prefs.getString(Prefs.SERVER_ADRR, ""));
        serverPort.setText(String.valueOf(prefs.getInt(Prefs.SERVER_PORT, 22)));
        username.setText(prefs.getString(Prefs.USERNAME, "root"));
        password.setText(prefs.getString(Prefs.PASSWORD, ""));
        proxyPort.setText(String.valueOf(prefs.getInt(Prefs.PROXY_PORT, 8119)));

        Toast.makeText(MainActivity.this, "Profile loaded", Toast.LENGTH_LONG).show();
    }

    public void onSaveProfile(View view) {
        String profileNameStr = profileName.getText().toString();
        String serverAddrStr = serverAddr.getText().toString();
        int serverPortNum = 22;
        try {
            serverPortNum = Integer.parseInt(serverPort.getText().toString());
        } catch (NumberFormatException e) {
        }
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        int proxyPortNum = 8119;
        try {
            proxyPortNum = Integer.parseInt(proxyPort.getText().toString());
        } catch (NumberFormatException e) {
        }

        SharedPreferences namePrefs = getSharedPreferences(Prefs.PROFILE_PREFS_NAME, MODE_PRIVATE);
        namePrefs.edit().putString(Prefs.PROFILE_PROFILE_NAME, profileNameStr).commit();

        SharedPreferences prefs = getSharedPreferences(profileNameStr, MODE_PRIVATE);
        prefs.edit()
                .putString(Prefs.SERVER_ADRR, serverAddrStr)
                .putInt(Prefs.SERVER_PORT, serverPortNum)
                .putString(Prefs.USERNAME, usernameStr)
                .putString(Prefs.PASSWORD, passwordStr)
                .putInt(Prefs.PROXY_PORT, proxyPortNum)
                .commit();

        Toast.makeText(MainActivity.this, "Profile saved", Toast.LENGTH_LONG).show();
    }

}
