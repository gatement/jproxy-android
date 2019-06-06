package johnsonlau.net.jproxy;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.NotificationChannel;

import net.johnsonlau.jproxy.lib.ProxyMain;
import net.johnsonlau.jproxy.lib.conf.ProxySettings;

import johnsonlau.net.jproxy.impl.MyProxyLog;

public class ProxyService extends IntentService {

    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "jproxy";

    public ProxyService() {
        super("ProxyService");
    }

    public static void start(Context context, String serverAddr, int serverPort, String username, String password, int proxyPort) {
        Intent intent = new Intent(context, ProxyService.class);
        intent.putExtra(Prefs.SERVER_ADRR, serverAddr);
        intent.putExtra(Prefs.SERVER_PORT, serverPort);
        intent.putExtra(Prefs.USERNAME, username);
        intent.putExtra(Prefs.PASSWORD, password);
        intent.putExtra(Prefs.PROXY_PORT, proxyPort);
        context.startForegroundService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String serverAddr = intent.getStringExtra(Prefs.SERVER_ADRR);
        final int serverPort = intent.getIntExtra(Prefs.SERVER_PORT, 22);
        final String username = intent.getStringExtra(Prefs.USERNAME);
        final String password = intent.getStringExtra(Prefs.PASSWORD);
        final int proxyPort = intent.getIntExtra(Prefs.PROXY_PORT, 8119);

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
                        .setContentText(getText(R.string.notification_message))
                        .setContentIntent(pendingIntent)
                        .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);

        ProxySettings settings = new ProxySettings(serverAddr, serverPort, username, password, proxyPort);
        new ProxyMain(settings, new MyProxyLog()).run();
    }

}
