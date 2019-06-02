package johnsonlau.net.jproxy.conf;


import net.johnsonlau.jproxy.lib.conf.ProxySettings;

public class MyProxySettings extends ProxySettings {

    @Override
    public String getSshHost() {
        return "47.52.27.162";
    }

    @Override
    public int getSshPort() {
        return 22;
    }

    @Override
    public String getSshUser() {
        return "root";
    }

    @Override
    public String getSshPwd() { return "ZX5E0zHeUfVsF7ZIw2lPAiQaQpgT"; }

    @Override
    public int getProxyPort() {
        return 8119;
    }
}

