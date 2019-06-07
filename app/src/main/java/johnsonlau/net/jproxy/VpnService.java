package johnsonlau.net.jproxy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import johnsonlau.net.jproxy.pojo.Prefs;

public class VpnService extends android.net.VpnService implements Handler.Callback {
    private static final int ONGOING_NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "jvpn";

    private static final String ACTION_CONNECT = "connect";
    private static final String ACTION_DISCONNECT = "disconnect";

    private Handler mHandler;

    private static class Connection extends Pair<Thread, ParcelFileDescriptor> {
        public Connection(Thread thread, ParcelFileDescriptor pfd) {
            super(thread, pfd);
        }
    }

    private final AtomicReference<Thread> mConnectingThread = new AtomicReference<>();
    private final AtomicReference<Connection> mConnection = new AtomicReference<>();

    private AtomicInteger mNextConnectionId = new AtomicInteger(1);

    private PendingIntent mConfigureIntent;

    @Override
    public void onCreate() {
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        mConfigureIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_CONNECT.equals(intent.getAction())) {
            connect(intent);
            return START_STICKY;
        } else {
            disconnect();
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        disconnect();
    }

    @Override
    public boolean handleMessage(Message message) {
        Toast.makeText(this, message.what, Toast.LENGTH_LONG).show();
        updateNotification(message.what);
        return true;
    }

    private void connect(Intent intent) {
        updateNotification(R.string.connecting);
        mHandler.sendEmptyMessage(R.string.connecting);

        final boolean allow = intent.getBooleanExtra(Prefs.ALLOW, true);
        final String packages = intent.getStringExtra(Prefs.PACKAGES);

        startConnection(new VpnConnection(this, allow, packages));
    }

    private void startConnection(final VpnConnection connection) {
        final Thread thread = new Thread(connection, "VpnThread");
        setConnectingThread(thread);

        connection.setConfigureIntent(mConfigureIntent);
        connection.setOnEstablishListener(tunInterface -> {
            mHandler.sendEmptyMessage(R.string.connected);

            mConnectingThread.compareAndSet(thread, null);
            setConnection(new Connection(thread, tunInterface));
        });
        thread.start();
    }

    private void setConnectingThread(final Thread thread) {
        final Thread oldThread = mConnectingThread.getAndSet(thread);
        if (oldThread != null) {
            oldThread.interrupt();
        }
    }

    private void setConnection(final Connection connection) {
        final Connection oldConnection = mConnection.getAndSet(connection);
        if (oldConnection != null) {
            try {
                oldConnection.first.interrupt();
                oldConnection.second.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnect() {
        Toast.makeText(this, R.string.disconnected, Toast.LENGTH_LONG).show();

        setConnectingThread(null);
        setConnection(null);
        stopForeground(true);
    }

    private void updateNotification(int msgId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT));
        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getString(msgId))
                .setContentIntent(mConfigureIntent)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }
}
