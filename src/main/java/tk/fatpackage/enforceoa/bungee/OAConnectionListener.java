package tk.fatpackage.enforceoa.bungee;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.bungee.modules.player.events.ClientConnectEvent;
import com.craftmend.openaudiomc.bungee.modules.player.events.ClientDisconnectEvent;
import com.craftmend.openaudiomc.generic.objects.OpenAudioApi;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class OAConnectionListener implements Listener {

    private OpenAudioApi oa = OpenAudioMc.getApi();
    private ProxyServer proxy = ProxyServer.getInstance();

    @EventHandler
    public void onClientConnect(ClientConnectEvent e) {
        log(proxy.getPlayer(e.getConnectedClient().getOwnerUUID()) + " connected to audio client");
        sendConnectionStatus(proxy.getPlayer(e.getConnectedClient().getOwnerUUID()), true);
    }

    @EventHandler
    public void onClientDisconnect(ClientDisconnectEvent e) {
        log(proxy.getPlayer(e.getDisconnectedClient().getOwnerUUID()) + " disconnected from audio client");
        sendConnectionStatus(proxy.getPlayer(e.getDisconnectedClient().getOwnerUUID()), false);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        log("Got plugin message of tag " + e.getTag());
        if (!e.getTag().equalsIgnoreCase("enforceoa:channel")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String subChannel = in.readUTF();
        log("Got plugin message on channel enforceoa:channel, subChannel " + subChannel);
        if (subChannel.equalsIgnoreCase("ConnectedToAudio")) {
            if (e.getReceiver() instanceof ProxiedPlayer) {
                log("Received plugin message ConnectedToAudio from player " + ((ProxiedPlayer) e.getReceiver()).getDisplayName());
                UUID uuid = ((ProxiedPlayer) e.getReceiver()).getUniqueId();
                sendConnectionStatus(proxy.getPlayer(uuid), oa.getClient(uuid).isConnected());
            }
        }
    }

    private void sendConnectionStatus(ProxiedPlayer p, boolean bool) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectedToAudio");
        out.writeBoolean(bool);
        p.sendData("enforceoa:channel", out.toByteArray());
        log("Send plugin message ConnectedToAudio to " + p.getDisplayName() + " : " + bool);
    }

    private void log(String msg) {
        proxy.getLogger().info(msg);
    }

}
