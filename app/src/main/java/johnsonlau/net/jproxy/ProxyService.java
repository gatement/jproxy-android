package johnsonlau.net.jproxy;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import net.johnsonlau.jproxy.lib.ProxyServer;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

import johnsonlau.net.jproxy.impl.MyProxyLog;
import johnsonlau.net.jproxy.pojo.Prefs;
import johnsonlau.net.jproxy.pojo.Profile;

public class ProxyService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "jproxy";
    private static final String ACTION_CONNECT = "connect";
    private static final String ACTION_DISCONNECT = "disconnect";

    private Thread mThread = null;

    public static void start(Context context, Profile profile) {
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_CONNECT);
        intent.putExtra(Prefs.SERVER_ADRR, profile.getServerAddr());
        intent.putExtra(Prefs.SERVER_PORT, profile.getServerPort());
        intent.putExtra(Prefs.USERNAME, profile.getUsername());
        intent.putExtra(Prefs.PASSWORD, profile.getPassword());
        intent.putExtra(Prefs.PROXY_PORT, profile.getProxyPort());
        context.startForegroundService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ProxyService.class);
        intent.setAction(ACTION_DISCONNECT);
        context.startService(intent);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_CONNECT.equals(intent.getAction())) {
            connect(intent);
            updateNotification("connected");
            return START_STICKY;
        } else {
            disconnect();
            return START_NOT_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null && ACTION_CONNECT.equals(intent.getAction())) {
            connect(intent);
        } else {
            disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void connect(Intent intent) {
        disconnect();

        final String serverAddr = intent.getStringExtra(Prefs.SERVER_ADRR);
        final int serverPort = intent.getIntExtra(Prefs.SERVER_PORT, 22);
        final String username = intent.getStringExtra(Prefs.USERNAME);
        final String password = intent.getStringExtra(Prefs.PASSWORD);
        final int proxyPort = intent.getIntExtra(Prefs.PROXY_PORT, 8119);
        ProxySettings settings = new ProxySettings(serverAddr, serverPort, username, password, proxyPort);

        mThread = new Thread(new ProxyServer(settings, new MyProxyLog()), "ProxyThread");
        mThread.start();
    }

    private void disconnect() {
        stopForeground(true);
        String message = "disconnected.";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (mThread != null) {
            mThread.interrupt();
            try {
                Thread.sleep(2000); // waiting for the thread to exit
            } catch (InterruptedException ex) { }
        }
    }

    private void updateNotification(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT));
        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

}
