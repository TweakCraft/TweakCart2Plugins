/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tweakcraft.tweakcart.cartcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.tweakcraft.tweakcart.api.event.TweakVehicleBlockChangeEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehicleDestroyEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

/**
 *
 * @author lennart
 */
public class SpeedControl extends TweakSignEventListener {
    
    private final Map<Minecart, SpeedDirective> speedControlledCarts = new HashMap<Minecart, SpeedDirective>();
    private final Pattern speedControlPattern = Pattern.compile("(\\d+)%(@(\\d+))?");
    private final CartControl plugin;

    public SpeedControl(CartControl plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        //  setting constant speed is reserved for signs attached to goldblocks
        Sign s = event.getSign();
        
        Block minecartBlock = event.getMinecart().getLocation().getBlock();
        
        if (minecartBlock.getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK 
                || minecartBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK) {
            // Parse that sign, reveal its information
            SpeedDirective sp = parseSign(s, event.getMinecart().getVelocity());
            
            if (sp != null) {
                // If parsing did succeed, lets control the minecart
                
                // first clear other directives
                if(speedControlledCarts.containsKey(event.getMinecart())){
                    speedControlledCarts.remove(event.getMinecart());
                }
                speedControlledCarts.put(event.getMinecart(), sp);
                event.getMinecart().setVelocity(event.getMinecart().getVelocity().normalize().multiply(sp.getSpeed()));
                sp.decrBlocks();
            }
        }
    }
    
    @Override
    public void onVehicleBlockChange(TweakVehicleBlockChangeEvent event) {
        SpeedDirective sp;
        if ((sp = speedControlledCarts.get(event.getMinecart())) != null) {
            event.getMinecart().setVelocity(event.getMinecart().getVelocity().normalize().multiply(sp.getSpeed()));
            sp.decrBlocks();
            if (sp.getBlocks() == 0) {
                speedControlledCarts.remove(event.getMinecart());
            }
        }
    }

    @Override
    public void onVehicleDestroy(TweakVehicleDestroyEvent event){
        if(speedControlledCarts.containsKey(event.getMinecart())){
            speedControlledCarts.remove(event.getMinecart());
        }
    }
    
    public SpeedDirective parseSign(Sign s, Vector velocity) {
        SpeedDirective result = null;
        for (String line : s.getLines()) {
            Matcher match = speedControlPattern.matcher(line);
            if (match.find()) {
                double speedMod = Math.min(this.plugin.getConfig().getDouble("speedcontrol.max-multiplier"), ((double) Integer.parseInt(match.group(1))) / 100.0);
                if (match.group(3) != null) {
                    int blocks = Math.min(this.plugin.getConfig().getInt("speedcontrol.max-blocks-controlled"), Integer.parseInt(match.group(3)));
                    result = new SpeedDirective(velocity.multiply(speedMod).length(), blocks);
                } else {
                    result = new SpeedDirective(velocity.multiply(speedMod).length(), 16);
                }
            }
        }
        
        return result;
    }
}