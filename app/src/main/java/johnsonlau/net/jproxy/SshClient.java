package johnsonlau.net.jproxy;

import java.io.IOException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshClient {
    private static JSch sshClient;
    public static Session sshSession;

    public static void connect() {
        try {
            closeSession();
            sshClient = new JSch();
            sshSession = sshClient.getSession(Settings.SSH_USER, Settings.SSH_HOST, Settings.SSH_PORT);
            sshSession.setPassword(Settings.SSH_PWD);
            sshSession.setConfig("StrictHostKeyChecking", "no"); // ask | yes | no
            sshSession.setServerAliveCountMax(3);
            sshSession.setServerAliveInterval(30000);
            sshSession.setDaemonThread(true);
            sshSession.connect();
            Util.log("SSH client version: " + sshSession.getClientVersion() + ", server version: "
                    + sshSession.getServerVersion());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Channel getStreamForwarder(String targetHost, int targetPort, boolean retrying)
            throws JSchException, IOException {
        try {
            Channel channel = SshClient.sshSession.getStreamForwarder(targetHost, targetPort);
            channel.connect(5000);
            return channel;
        } catch (JSchException ex) {
            // session is down
            if (!retrying) {
                Util.log("Reconnecting SSH Tunnel");
                connect();
                return getStreamForwarder(targetHost, targetPort, true);
            } else {
                throw ex;
            }
        }
    }

    private static void closeSession() {
        if (sshSession != null) {
            try {
                sshSession.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}

