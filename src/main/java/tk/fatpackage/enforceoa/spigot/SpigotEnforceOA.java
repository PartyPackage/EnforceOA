package tk.fatpackage.enforceoa.spigot;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotEnforceOA extends JavaPlugin {

    @Getter private static SpigotEnforceOA instance;
    @Getter private boolean isUnderBungee;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (!getServer().getVersion().contains("Spigot") && !getServer().getVersion().contains("Paper") && !getServer().getVersion().contains("Yatopia")) {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }
        isUnderBungee = !getServer().getOnlineMode();
        if (isUnderBungee) {
            getLogger().info("BungeeCord detected, enabling BungeeCord support");
            getServer().getMessenger().registerIncomingPluginChannel(this, "enforceoa:channel", new CustomPluginMessageListener());
        }
        getServer().getPluginManager().registerEvents(new OAConnectionListener(), this);
    }

}
