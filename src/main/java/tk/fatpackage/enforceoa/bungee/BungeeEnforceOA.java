package tk.fatpackage.enforceoa.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeEnforceOA extends Plugin {
    @Override
    public void onEnable() {
        getProxy().registerChannel("enforceoa:channel");
        getProxy().getPluginManager().registerListener(this, new OAConnectionListener());
    }
}
