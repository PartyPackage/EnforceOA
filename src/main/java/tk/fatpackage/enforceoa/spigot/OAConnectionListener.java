package tk.fatpackage.enforceoa.spigot;

import com.craftmend.openaudiomc.spigot.modules.players.events.ClientConnectEvent;
import com.craftmend.openaudiomc.spigot.modules.players.events.ClientDisconnectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OAConnectionListener implements Listener {

    private SpigotEnforceOA plugin = SpigotEnforceOA.getInstance();
    private boolean isUnderBungee = SpigotEnforceOA.getInstance().isUnderBungee();
    private PlayerManager pm = PlayerManager.getInstance();

    @EventHandler
    public void onClientConnect(ClientConnectEvent e) {
        pm.enablePlayer(e.getPlayer());
    }

    @EventHandler
    public void onClientDisconnect(ClientDisconnectEvent e) {
        pm.disablePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!isUnderBungee) {
            // standalone server, so they can't possibly be connected to the audio client yet
            pm.disablePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        pm.enablePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (pm.disabledPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            e.getRecipients().removeAll(pm.disabledPlayers);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (pm.disabledPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
