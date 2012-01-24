package net.tweakcraft.tweakcart2cartstorage;

import net.tweakcraft.tweakcart.TweakPluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TweakCartCartStorage extends JavaPlugin {
    TweakPluginManager corePluginManager = TweakPluginManager.getInstance();

    @Override
    public void onDisable() {
        //Has to be implemented
    }

    @Override
    public void onEnable() {
        //corePluginManager.registerEvent(...);
    }
}
