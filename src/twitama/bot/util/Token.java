package twitama.bot.util;

/**
 * AccessToken/Secretをデータストアに格納するためのクラス
 * @author kohta
 *
 */
import java.io.Serializable;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class Token implements Serializable {

    public Token() {
        super();
    }
    public Token(String botName, String accessToken, String accessSecret) {
        super();
        this.botName = botName;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String botName;

    @Persistent
    private String accessToken;

    @Persistent
    private String accessSecret;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

}

