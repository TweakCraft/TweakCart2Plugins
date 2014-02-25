/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.tweakcraft.tweakcart.cartcontrol;

import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 *
 * @author lennart
 */
public class DeadStop extends TweakSignEventListener {

    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        Block minecartBlock = event.getMinecart().getLocation().getBlock();

        if (minecartBlock.getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN 
                || minecartBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN) {
            event.getMinecart().setVelocity(new Vector(0.0,0.0,0.0));
        }
    }
    
}
