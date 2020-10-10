package tk.fatpackage.enforceoa.spigot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerManager {

    private static PlayerManager pm;
    private SpigotEnforceOA plugin = SpigotEnforceOA.getInstance();
    public Map<Player, PlayerInventoryEquipment> disabledPlayers = new HashMap<>();
    public Map<Player, BukkitTask> taskMap = new HashMap<>();
    private long kickDelay = plugin.getConfig().getInt("kick-delay-seconds") * 20;
    private String kickMsg = plugin.getConfig().getString("kick-msg");
    private Collection<PotionEffect> potionEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false),
            new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128, true, false),
            new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false));

    public static PlayerManager getInstance() {
        if (pm == null) {
            pm = new PlayerManager();
        }
        return pm;
    }

    private PlayerManager() {}

    public void disablePlayer(Player p) {
        if (p.hasPermission("enforceoa.bypass")) {
            return;
        }
        // Disable the player 1 tick after joining
        Bukkit.getScheduler().runTaskLater(SpigotEnforceOA.getInstance(), () -> {
            if (!disabledPlayers.containsKey(p)) {
                ItemStack[] inv = p.getInventory().getContents();
                ItemStack[] equip = p.getInventory().getArmorContents();
                ItemStack[] extra = p.getInventory().getExtraContents();
                disabledPlayers.put(p, new PlayerInventoryEquipment(inv, equip, extra));
                p.getInventory().clear();
                p.addPotionEffects(potionEffects);
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.kickPlayer(kickMsg);
                    }
                }.runTaskLater(plugin, kickDelay); // kick after 1.5 minutes
                taskMap.putIfAbsent(p, task);
            }
        }, 1L);
    }

    public void disablePlayer(Player p, String url) {
        if (p.hasPermission("enforceoa.bypass")) {
            return;
        }

        disablePlayer(p);

        // async generate qr code
        if (isFloodgatePlayer(p)) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                MapView view = Bukkit.createMap(p.getWorld());

                // sync give item
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ItemStack item = new ItemStack(Material.MAP, 1, view.getId());
                    MapMeta meta = (MapMeta) item.getItemMeta();
                    view.setScale(MapView.Scale.NORMAL);
                    view.setUnlimitedTracking(false);
                    view.getRenderers().clear();
                    MapRenderer renderer = new QRMapRenderer(url);
                    view.addRenderer(renderer);
                    item.setItemMeta(meta);
                    p.getInventory().setItem(4, item);
                    p.getInventory().setHeldItemSlot(4);
                });
            });
        }
    }

    public void enablePlayer(Player p) {
        if (!disabledPlayers.containsKey(p)) {
            return;
        }
        potionEffects.forEach(potionEffect -> {
            p.removePotionEffect(potionEffect.getType());
        });
        p.getInventory().clear();
        PlayerInventoryEquipment playerInventoryEquipment = disabledPlayers.get(p);
        p.getInventory().setContents(playerInventoryEquipment.getInv());
        p.getInventory().setArmorContents(playerInventoryEquipment.getEquip());
        p.getInventory().setExtraContents(playerInventoryEquipment.getExtra());
        disabledPlayers.remove(p);
        if (taskMap.containsKey(p)) {
            taskMap.get(p).cancel();
            taskMap.remove(p);
        }
    }

    public boolean isFloodgatePlayer(Player p) {
        return p.getUniqueId().getMostSignificantBits() == 0;
    }
}
