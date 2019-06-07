package johnsonlau.net.jproxy;

import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.net.ProxyInfo;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.util.Set;

public class VpnConnection implements Runnable {
    public interface OnEstablishListener {
        void onEstablish(ParcelFileDescriptor tunInterface);
    }

    private final VpnService mService;
    private final int mConnectionId;

    private PendingIntent mConfigureIntent;
    private OnEstablishListener mOnEstablishListener;

    private String mProxyAddr;
    private int mProxyPort;
    private final boolean mAllow;
    private final Set<String> mPackages;

    public VpnConnection(final VpnService service,
                         final int connectionId,
                         final int proxyPort,
                         final boolean allow,
                         final Set<String> packages) {
        mService = service;
        mConnectionId = connectionId;
        mProxyAddr = "localhost";
        mProxyPort = proxyPort;
        mAllow = allow;
        mPackages = packages;
    }

    public void setConfigureIntent(PendingIntent intent) {
        mConfigureIntent = intent;
    }

    public void setOnEstablishListener(OnEstablishListener listener) {
        mOnEstablishListener = listener;
    }

    @Override
    public void run() {
        ParcelFileDescriptor iface = null;
        try {
            iface = buildTunInterface();
            while (true) {
                Thread.sleep(1000); // allow for being interrupted
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (iface != null) {
                try {
                    iface.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private ParcelFileDescriptor buildTunInterface() throws IllegalArgumentException {
        final ParcelFileDescriptor vpnInterface;
        VpnService.Builder builder = mService.new Builder();

        builder.addAddress("10.2.3.4", 24);
        builder.addRoute("0.0.0.0", 0);
        builder.setSession("jproxy").setConfigureIntent(mConfigureIntent);
        builder.setHttpProxy(ProxyInfo.buildDirectProxy(mProxyAddr, mProxyPort));

        for (String packageName : mPackages) {
            try {
                if (mAllow) {
                    builder.addAllowedApplication(packageName);
                } else {
                    builder.addDisallowedApplication(packageName);
                    builder.addDisallowedApplication("johnsonlau.net.jproxy");
                }
            } catch (PackageManager.NameNotFoundException ex){
                ex.printStackTrace();
            }
        }

        synchronized (mService) {
            vpnInterface = builder.establish();
            if (mOnEstablishListener != null) {
                mOnEstablishListener.onEstablish(vpnInterface);
            }
        }

        return vpnInterface;
    }
}
