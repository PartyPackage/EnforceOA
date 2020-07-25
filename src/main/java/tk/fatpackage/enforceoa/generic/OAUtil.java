package tk.fatpackage.enforceoa.generic;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.networking.client.objects.player.ClientConnection;

import java.util.Base64;

public class OAUtil {

    private static OAUtil instance;
    private OpenAudioMc openAudioMc = OpenAudioMc.getInstance();

    public static OAUtil getInstance() {
        if (instance == null) {
            instance = new OAUtil();
        }
        return instance;
    }

    private OAUtil() {}

    public String getOldURL(ClientConnection client) {
        String token = client.getPlayer().getName() +
                ":" +
                client.getPlayer().getUniqueId().toString() +
                ":" +
                openAudioMc.getAuthenticationService().getServerKeySet().getPublicKey().getValue() +
                ":" +
                client.getSession().getKey();
        String baseUrl = openAudioMc.getPlusService().getBaseUrl();
        return baseUrl.substring(0, baseUrl.length() - 1) + "?&data=" + new String(Base64.getEncoder().encode(token.getBytes()));
    }

}
