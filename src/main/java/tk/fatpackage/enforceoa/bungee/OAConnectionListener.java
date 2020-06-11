package tk.fatpackage.enforceoa.bungee;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.objects.OpenAudioApi;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OAConnectionListener implements Listener {

    private OpenAudioApi oa = OpenAudioMc.getApi();

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (e.getFrom() == null) {
            // first time connecting so they can't possibly be connected
            sendConnectionStatus(p, false);
        } else {
            sendConnectionStatus(p, oa.getClient(p.getUniqueId()).isConnected());
        }
    }

    private void sendConnectionStatus(ProxiedPlayer p, boolean bool) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectedToAudio");
        out.writeUTF(p.getUniqueId().toString());
        out.writeBoolean(bool);
        p.getServer().getInfo().sendData("enforceoa:channel", out.toByteArray());
    }

}
