package tk.fatpackage.enforceoa.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CustomPluginMessageListener implements PluginMessageListener {

    private PlayerManager pm = PlayerManager.getInstance();
    private SpigotEnforceOA plugin = SpigotEnforceOA.getInstance();

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        plugin.log("Received plugin message on channel " + channel + " for player " + p.getDisplayName());
        if (!channel.equalsIgnoreCase("enforceoa:channel")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        plugin.log("Sub Channel: " + subChannel);
        if (subChannel.equalsIgnoreCase("ConnectedToAudio")) {
            boolean isConnected = in.readBoolean();
            plugin.log("Received plugin message ConnectedToAudio for player " + p.getDisplayName() + " : " + isConnected);
            if (isConnected) {
                pm.enablePlayer(p);
            } else {
                pm.disablePlayer(p);
            }
        }
    }
}
