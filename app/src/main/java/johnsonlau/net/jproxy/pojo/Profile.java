package johnsonlau.net.jproxy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    private String profileName;
    private String serverAddr;
    private int serverPort;
    private String username;
    private String password;
    private int proxyPort;
}
