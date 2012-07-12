/*
 * Copyright (c) 2012.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package net.tweakcraft.tweakcart.debug;

import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.api.event.*;
import net.tweakcraft.tweakcart.api.event.listeners.TweakBlockEventListener;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.api.model.TweakCartEvent;
import net.tweakcraft.tweakcart.api.model.TweakCartPlugin;
import net.tweakcraft.tweakcart.api.model.TweakPermissionsHandler;
import net.tweakcraft.tweakcart.api.util.TweakPermissionsManager;
import net.tweakcraft.tweakcart.util.TweakPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Debug extends TweakCartPlugin {

    private DebugBlockListener blockListener = new DebugBlockListener();
    private DebugSignListener signListener = new DebugSignListener();
    private DebugSignKeywordListener signKeywordListener = new DebugSignKeywordListener();
    private DebugPermissionsHandler debugPermissionsHandler = new DebugPermissionsHandler();

    @Override
    public void onEnable() {
        super.onEnable();

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(blockListener, this);
    }

    @Override
    public String getPluginName() {
        return "Debug";
    }

    @Override
    public void registerEvents(TweakPluginManager pluginManager) {
        if (TweakCart.DEBUG) {
            pluginManager.registerEvent(blockListener, TweakCartEvent.Block.values());
            for (TweakCartEvent.Sign signEvent : TweakCartEvent.Sign.values()) {
                pluginManager.registerEvent(signListener, signEvent);
                pluginManager.registerEvent(signKeywordListener, signEvent, "a");
            }
            TweakPermissionsManager.getInstance().addHandler(debugPermissionsHandler);
        }
    }

    public class DebugBlockListener extends TweakBlockEventListener implements Listener {

        public void onVehicleBlockChange(TweakVehicleBlockChangeEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehicleBlockChange event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Data: " + event.getBlock().getType().name());
        }

        public void onVehicleBlockCollision(TweakVehicleBlockCollisionEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehicleBlockCollision event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Data: " + event.getBlock().getType().name());
        }

        public void onVehicleDetect(TweakVehicleBlockDetectEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehicleBlockDetect event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Data: " + event.getBlock().getType().name());
        }

        @EventHandler
        public void onVehicleDestroy(VehicleDestroyEvent ev) {
            if (ev.getVehicle() instanceof StorageMinecart) {
                ItemStack[] leftovers = ((StorageMinecart) ev.getVehicle()).getInventory().getContents();
                for(ItemStack is : leftovers) {
                    if(is!=null)
                        ((StorageMinecart) ev.getVehicle()).getWorld().dropItemNaturally(((StorageMinecart) ev.getVehicle()).getLocation(), is);
                }

                ev.getVehicle().getWorld().dropItem(ev.getVehicle().getLocation(), new ItemStack(Material.STORAGE_MINECART, 1));
                ev.getVehicle().remove();
                ev.setCancelled(true);
            }
        }
    }

    private class DebugSignListener extends TweakSignEventListener {

        public void onSignPass(TweakVehiclePassesSignEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehiclePassesSign event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "First line: " + event.getSign().getLine(0));
        }

        public void onSignCollision(TweakVehicleCollidesWithSignEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehicleCollidesWithSign event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "First line: " + event.getSign().getLine(0));
        }
    }

    private class DebugSignKeywordListener extends TweakSignEventListener {

        public void onSignPass(TweakVehiclePassesSignEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehiclePassesSign event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Keyword: " + event.getKeyword());
        }

        public void onSignCollision(TweakVehicleCollidesWithSignEvent event) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "VehicleCollidesWithSign event thrown");
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Keyword: " + event.getKeyword());
        }
    }

    private class DebugPermissionsHandler implements TweakPermissionsHandler {

        @Override
        public boolean canVehicleCollect(TweakVehicleCollectEvent tweakVehicleCollectEvent) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Permission request: VehicleCollect");
            return true;
        }

        @Override
        public boolean canDispense(TweakVehicleDispenseEvent tweakVehicleDispenseEvent) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Permission request: VehicleDispense");
            return true;
        }

        @Override
        public boolean canSlapCollect(TweakPlayerCollectEvent tweakPlayerCollectEvent) {
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Permission request: PlayerCollect");
            return true;
        }

        @Override
        public String getName() {
            return "Debug";
        }
    }
}
