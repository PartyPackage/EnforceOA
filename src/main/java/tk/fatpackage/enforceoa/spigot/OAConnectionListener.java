package tk.fatpackage.enforceoa.spigot;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.spigot.modules.players.events.ClientConnectEvent;
import com.craftmend.openaudiomc.spigot.modules.players.events.ClientDisconnectEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import tk.fatpackage.enforceoa.generic.OAUtil;

public class OAConnectionListener implements Listener {

    private boolean isUnderBungee = SpigotEnforceOA.getInstance().isUnderBungee();
    private PlayerManager pm = PlayerManager.getInstance();
    private OpenAudioMc openAudioMc = OpenAudioMc.getInstance();

    @EventHandler
    public void onClientConnect(ClientConnectEvent e) {
        pm.enablePlayer(e.getPlayer());
    }

    @EventHandler
    public void onClientDisconnect(ClientDisconnectEvent e) {
        if (!isUnderBungee) {
            Player p = e.getPlayer();
            String url = OAUtil.getInstance().getOldURL(openAudioMc.getNetworkingService().getClient(p.getUniqueId()));
            pm.disablePlayer(p, url);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!isUnderBungee) {
            // standalone server, so they can't possibly be connected to the audio client yet
            Player p = e.getPlayer();
            String url = OAUtil.getInstance().getOldURL(openAudioMc.getNetworkingService().getClient(p.getUniqueId()));
            pm.disablePlayer(p, url);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        pm.enablePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (pm.disabledPlayers.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        } else {
            e.getRecipients().removeAll(pm.disabledPlayers.keySet());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (pm.disabledPlayers.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (pm.disabledPlayers.containsKey(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (pm.disabledPlayers.containsKey(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSlotSelect(PlayerItemHeldEvent e) {
        if (pm.disabledPlayers.containsKey(e.getPlayer()) && e.getNewSlot() != 4) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (pm.disabledPlayers.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
