package tk.fatpackage.enforceoa.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeEnforceOA extends Plugin {
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new OAConnectionListener());
        getProxy().registerChannel("enforceoa:channel");
        getLogger().info("Registered plugin message channel enforceoa:channel");
    }
}
