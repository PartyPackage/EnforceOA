package tk.fatpackage.enforceoa.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CustomPluginMessageListener implements PluginMessageListener {

    private PlayerManager pm = PlayerManager.getInstance();

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        if (!channel.equalsIgnoreCase("enforceoa:channel")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("ConnectedToAudio")) {
            boolean isConnected = in.readBoolean();
            if (isConnected) {
                pm.enablePlayer(p);
            } else {
                pm.disablePlayer(p);
            }
        }
    }
}
