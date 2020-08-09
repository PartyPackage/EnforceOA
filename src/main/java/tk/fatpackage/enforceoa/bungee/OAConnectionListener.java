package tk.fatpackage.enforceoa.bungee;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.api.interfaces.AudioApi;
import com.craftmend.openaudiomc.api.interfaces.Client;
import com.craftmend.openaudiomc.bungee.modules.player.events.ClientDisconnectEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.fatpackage.enforceoa.generic.OAUtil;

public class OAConnectionListener implements Listener {

    private AudioApi audioApi = AudioApi.getInstance();
    private OpenAudioMc openAudioMc = OpenAudioMc.getInstance();

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String url = OAUtil.getInstance().getOldURL(openAudioMc.getNetworkingService().getClient(p.getUniqueId()));
        Client client = audioApi.getClient(p.getUniqueId());
        boolean isConnected = false;
        if (client != null) {
            isConnected = client.isConnected();
        }

        if (e.getFrom() == null) {
            // first time connecting so they can't possibly be connected
            sendConnectionStatus(p, false, url);
        } else {
            sendConnectionStatus(p, isConnected, url);
        }
    }

    @EventHandler
    public void onAudioClientDisconnect(ClientDisconnectEvent e) {
        ProxyServer.getInstance().getLogger().info(e.getDisconnectedClient().getOwnerName() + " disconnected from audio");
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getDisconnectedClient().getOwnerUUID());
        String url = OAUtil.getInstance().getOldURL(openAudioMc.getNetworkingService().getClient(p.getUniqueId()));
        sendConnectionStatus(p, e.getDisconnectedClient().getIsConnected(), url);
    }

    private void sendConnectionStatus(ProxiedPlayer p, boolean bool, String url) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectedToAudio");
        out.writeUTF(p.getUniqueId().toString());
        out.writeBoolean(bool);
        out.writeUTF(url);
        p.getServer().getInfo().sendData("enforceoa:channel", out.toByteArray());
    }

}
