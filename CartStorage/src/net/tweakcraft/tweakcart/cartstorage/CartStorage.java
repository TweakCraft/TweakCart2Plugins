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

package net.tweakcraft.tweakcart.cartstorage;

import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.api.model.TweakCartEvent;
import net.tweakcraft.tweakcart.api.model.TweakCartPlugin;
import net.tweakcraft.tweakcart.model.IntMap;
import net.tweakcraft.tweakcart.util.ChestUtil;
import net.tweakcraft.tweakcart.util.InventoryManager;
import net.tweakcraft.tweakcart.util.TweakPluginManager;
import org.bukkit.block.Chest;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class CartStorage extends TweakCartPlugin {
    CartStorageEventListener eventListener = new CartStorageEventListener();

    @Override
    public String getPluginName() {
        return "CartStorage";
    }

    @Override
    public void registerEvents(TweakPluginManager pluginManager) {
        pluginManager.registerEvent(eventListener, TweakCartEvent.Sign.VehiclePassesSignEvent, "collect items", "deposit items");
    }

    @Override
    public void onEnable() {
    }

    /**
     * Created by IntelliJ IDEA.
     *
     * @author Edoxile
     */
    public static class CartStorageEventListener extends TweakSignEventListener {
        @Override
        public void onSignPass(TweakVehiclePassesSignEvent event) {
            if (event.getMinecart() instanceof StorageMinecart) {
                StorageMinecart storageMinecart = (StorageMinecart) event.getMinecart();
                Inventory cartInventory = storageMinecart.getInventory();
                IntMap[] maps = ItemParser.parseSign(event.getSign(), event.getDirection());
                if (maps != null) {
                    List<Chest> chestList = ChestUtil.getChestsAroundBlock(event.getSign().getBlock(), 1);
                    for (Chest c : chestList) {
                        InventoryManager.moveContainerContents(cartInventory, c.getInventory(), maps);
                        /**
                         * TODO: we still have to do something with the return data of this function. For example,
                         * if the cart is empty, we can omit the storing in chests for at least one round. Also,
                         * we should explain to the people which comes first, collect or deposit. It is of importance
                         * in some situations.
                         */
                    }
                }
            }
        }
    }
}
