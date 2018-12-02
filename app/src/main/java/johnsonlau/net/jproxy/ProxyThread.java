package johnsonlau.net.jproxy;

import net.johnsonlau.jproxy.lib.ProxyMain;

import johnsonlau.net.jproxy.conf.MyProxyLog;
import johnsonlau.net.jproxy.conf.MyProxySettings;

public class ProxyThread extends Thread {
    @Override
    public void run() {
        new ProxyMain(new MyProxySettings(), new MyProxyLog()).run();
    }
}
