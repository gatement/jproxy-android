package johnsonlau.net.jproxy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import johnsonlau.net.jproxy.pojo.Prefs;
import johnsonlau.net.jproxy.pojo.Profile;

public class MainActivity extends AppCompatActivity {

    private EditText profileNameCtl;
    private EditText serverAddrCtl;
    private EditText serverPortCtl;
    private EditText usernameCtl;
    private EditText passwordCtl;
    private EditText proxyPortCtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get controls
        profileNameCtl = findViewById(R.id.profileName);
        serverAddrCtl = findViewById(R.id.serverAddr);
        serverPortCtl = findViewById(R.id.serverPort);
        usernameCtl = findViewById(R.id.username);
        passwordCtl = findViewById(R.id.password);
        proxyPortCtl = findViewById(R.id.proxyPort);

        // load UI from profile
        Profile profile = getProfileFromPreference();
        loadUIFromProfile(profile);
    }

    public void onStartProxy(View view){
        Profile profile = getProfileFromUI();
        ProxyService.start(this, profile);
        Toast.makeText(MainActivity.this, "Starting HTTP Proxy Server", Toast.LENGTH_LONG).show();
    }

    public void onStopProxy(View view){
        ProxyService.stop(this);
        Toast.makeText(MainActivity.this, "Stopping HTTP Proxy Server", Toast.LENGTH_LONG).show();
    }

    public void onLoadProfile(View view){
        String profileName = profileNameCtl.getText().toString();
        loadUIFromProfileName(profileName);
        Toast.makeText(MainActivity.this, "Profile loaded", Toast.LENGTH_LONG).show();
    }

    public void onSaveProfile(View view) {
        Profile profile = getProfileFromUI();
        saveProfile(profile);
        Toast.makeText(MainActivity.this, "Profile saved", Toast.LENGTH_LONG).show();
    }

    private void saveProfile(Profile profile) {
        String profileName = profile.getProfileName();

        // save profileName
        SharedPreferences namePrefs = getSharedPreferences(Prefs.PROFILE_PREFS_NAME, MODE_PRIVATE);
        namePrefs.edit().putString(Prefs.PROFILE_PROFILE_NAME, profileName).commit();

        // save profile values
        SharedPreferences prefs = getSharedPreferences(profileName, MODE_PRIVATE);
        prefs.edit()
                .putString(Prefs.SERVER_ADDR, profile.getServerAddr())
                .putInt(Prefs.SERVER_PORT, profile.getServerPort())
                .putString(Prefs.USERNAME, profile.getUsername())
                .putString(Prefs.PASSWORD, profile.getPassword())
                .putInt(Prefs.PROXY_PORT, profile.getProxyPort())
                .commit();
    }

    private void loadUIFromProfileName(String profileName)  {
        Profile profile = getProfileFromPreference(profileName);
        loadUIFromProfile(profile);
    }

    private void loadUIFromProfile(Profile profile)  {
        profileNameCtl.setText(profile.getProfileName());
        serverAddrCtl.setText(profile.getServerAddr());
        serverPortCtl.setText(String.valueOf(profile.getServerPort()));
        usernameCtl.setText(profile.getUsername());
        passwordCtl.setText(profile.getPassword());
        proxyPortCtl.setText(String.valueOf(profile.getProxyPort()));
    }

    private Profile getProfileFromPreference() {
        // load profileName
        SharedPreferences namePrefs = getSharedPreferences(Prefs.PROFILE_PREFS_NAME, MODE_PRIVATE);
        String profileName = namePrefs.getString(Prefs.PROFILE_PROFILE_NAME, "default");

        return getProfileFromPreference(profileName);
    }

    private Profile getProfileFromPreference(String profileName) {
        Profile profile = new Profile();
        profile.setProfileName(profileName);

        // load profile values
        SharedPreferences prefs = getSharedPreferences(profileName, MODE_PRIVATE);
        profile.setServerAddr(prefs.getString(Prefs.SERVER_ADDR, ""));
        profile.setServerPort(prefs.getInt(Prefs.SERVER_PORT, 22));
        profile.setUsername(prefs.getString(Prefs.USERNAME, "root"));
        profile.setPassword(prefs.getString(Prefs.PASSWORD, ""));
        profile.setProxyPort(prefs.getInt(Prefs.PROXY_PORT, 8119));

        return profile;
    }

    private Profile getProfileFromUI() {
        Profile profile =  new Profile();

        profile.setProfileName(profileNameCtl.getText().toString().trim());
        profile.setServerAddr(serverAddrCtl.getText().toString().trim());
        profile.setUsername(usernameCtl.getText().toString().trim());
        profile.setPassword(passwordCtl.getText().toString().trim());

        int serverPortNum = 22;
        try {
            serverPortNum = Integer.parseInt(serverPortCtl.getText().toString().trim());
        } catch (NumberFormatException e) {
        }
        profile.setServerPort(serverPortNum);

        int proxyPortNum = 8119;
        try {
            proxyPortNum = Integer.parseInt(proxyPortCtl.getText().toString().trim());
        } catch (NumberFormatException e) {
        }
        profile.setProxyPort(proxyPortNum);

        return profile;
    }

}
