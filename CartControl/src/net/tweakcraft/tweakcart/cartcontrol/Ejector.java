/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tweakcraft.tweakcart.cartcontrol;

import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.model.Direction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;

/**
 *
 * @author lennart
 */
public class Ejector extends TweakSignEventListener {
    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        Block minecartBlock = event.getMinecart().getLocation().getBlock();
        Location l = event.getMinecart().getLocation();

        if (minecartBlock.getRelative(BlockFace.DOWN).getType() == Material.IRON_BLOCK
                || minecartBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.IRON_BLOCK) {
            if (event.getMinecart().getPassenger() != null) {
                Entity passenger = event.getMinecart().getPassenger();
                event.getMinecart().eject();
                               
                Direction signDir = parseDirection(event.getSign());
                passenger.teleport(minecartBlock.getLocation().add(signDir.getModX() + 0.5, signDir.getModY(), signDir.getModZ() + 0.5));
            }

        }
    }

    public Direction parseDirection(Sign s) {
        Direction dir = Direction.SELF;
        for (String line : s.getLines()) {
            if (Direction.getDirectionByName(line) != Direction.SELF) {
                return Direction.getDirectionByName(line);
            }
        }

        return dir;
    }
}
