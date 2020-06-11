package tk.fatpackage.enforceoa.spigot;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerManager {

    private static PlayerManager pm;
    private SpigotEnforceOA plugin = SpigotEnforceOA.getInstance();
    public List<Player> disabledPlayers = new ArrayList<>();
    public Map<Player, BukkitTask> taskMap = new HashMap<>();
    private long kickDelay = plugin.getConfig().getInt("kick-delay-seconds") * 20;
    private String kickMsg = plugin.getConfig().getString("kick-msg");
    private Collection<PotionEffect> potionEffects = Arrays.asList(
            new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false),
            new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 128, true, false),
            new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128, true, false),
            new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false),
            new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));

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

        if (!disabledPlayers.contains(p)) {
            disabledPlayers.add(p);
            p.addPotionEffects(potionEffects);
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    p.kickPlayer(kickMsg);
                }
            }.runTaskLater(plugin, kickDelay); // kick after 1.5 minutes
            taskMap.putIfAbsent(p, task);
        }
    }

    public void enablePlayer(Player p) {
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.JUMP);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        disabledPlayers.remove(p);
        if (taskMap.containsKey(p)) {
            taskMap.get(p).cancel();
            taskMap.remove(p);
        }
    }

}
