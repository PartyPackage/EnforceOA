package tk.fatpackage.enforceoa.spigot;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotEnforceOA extends JavaPlugin {

    @Getter private static SpigotEnforceOA instance;
    @Getter private boolean isUnderBungee;

    @Override
    public void onEnable() {
        instance = this;
        if ( !getServer().getVersion().contains("Spigot") && !getServer().getVersion().contains("Paper")) {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }
        //isUnderBungee = getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("settings.bungeecord");
        isUnderBungee = !getServer().getOnlineMode();
        if (isUnderBungee) {
            getLogger().info("BungeeCord detected, enabling BungeeCord support");
            getServer().getMessenger().registerIncomingPluginChannel(this, "enforceoa:channel", new CustomPluginMessageListener());
            getServer().getMessenger().registerOutgoingPluginChannel(this, "enforceoa:channel");
        }
        getServer().getPluginManager().registerEvents(new OAConnectionListener(), this);
    }

    public void log(String msg) {
        getLogger().info(msg);
    }
}
