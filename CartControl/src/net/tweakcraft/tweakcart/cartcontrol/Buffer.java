/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.tweakcraft.tweakcart.cartcontrol;

import net.tweakcraft.tweakcart.api.event.TweakSignRedstoneEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * @author lennart
 */
public class Buffer extends TweakSignEventListener {
    private HashMap<Sign, WaitSet> waitSet = new HashMap<Sign, WaitSet>();

    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        Block minecartBlock = event.getMinecart().getLocation().getBlock();

        if ((minecartBlock.getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN
                || minecartBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN)
                && !(event.getSign().getBlock().isBlockPowered())) {
            waitSet.put(event.getSign(), new WaitSet(event.getMinecart(), event.getMinecart().getVelocity()));
            event.getMinecart().setVelocity(new Vector(0.0, 0.0, 0.0));
        }
    }

    @Override
    public void onSignRedstone(TweakSignRedstoneEvent event) {
        if (event.getPower() > 0) {
            WaitSet w = waitSet.get(event.getSign());
            if (w != null) {
                w.getMinecart().setVelocity(w.getVelocity());
                waitSet.remove(event.getSign());
            }
        }
    }

    private class WaitSet {
        private final Minecart cart;
        private final Vector velocity;

        public WaitSet(Minecart c, Vector v) {
            cart = c;
            velocity = v;
        }

        public Minecart getMinecart() {
            return cart;
        }

        public Vector getVelocity() {
            return velocity;
        }
    }
}
