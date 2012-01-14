package net.tweakcraft.tweakcart2elevators;

import net.tweakcraft.tweakcart.TweakCart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TweakCartElevator extends JavaPlugin {

    @Override
    public void onDisable() {
        //Has to be implemented
    }

    @Override
    public void onEnable() {
        //TODO: Get TweakCart, initialize Elevators and register events...
        Plugin plugin = this.getServer().getPluginManager().getPlugin("TweakCart");
        new Elevator(((TweakCart)plugin)).onEnable();
    }
    
    public String toString(){
        return "TweakCartElevatorPlugin";
        
    }
}
