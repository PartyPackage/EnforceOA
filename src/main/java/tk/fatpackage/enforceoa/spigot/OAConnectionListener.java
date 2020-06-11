package tk.fatpackage.enforceoa.spigot;

import com.craftmend.openaudiomc.spigot.modules.players.events.ClientConnectEvent;
import com.craftmend.openaudiomc.spigot.modules.players.events.ClientDisconnectEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OAConnectionListener implements Listener {

    private boolean isUnderBungee = SpigotEnforceOA.getInstance().isUnderBungee();
    private SpigotEnforceOA plugin = SpigotEnforceOA.getInstance();
    private PlayerManager pm = PlayerManager.getInstance();

    @EventHandler
    public void onClientConnect(ClientConnectEvent e) {
        // Won't fire if under bungee
        pm.enablePlayer(e.getPlayer());
        plugin.log("OA ClientConnectEvent for " + e.getPlayer().getDisplayName());
    }

    @EventHandler
    public void onClientDisconnect(ClientDisconnectEvent e) {
        // Won't fire if under bungee
        pm.disablePlayer(e.getPlayer());
        plugin.log("OA ClientDisconnectEvent for " + e.getPlayer().getDisplayName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!isUnderBungee) {
            // Running under one spigot server
            pm.disablePlayer(p);
        } else {
            // Running in a bungee network
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ConnectedToAudio");
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendPluginMessage(plugin, "enforceoa:channel", out.toByteArray());
                    plugin.log("Sent ConnectedToAudio plugin message for player " + p.getDisplayName());
                }
            }.runTaskLater(SpigotEnforceOA.getInstance(), 5L);
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
