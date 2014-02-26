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

package net.tweakcraft.tweakcart.intersection.parser;

import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class IntersectionParser {
    public static Direction parseIntersection(String line, Minecart cart, Direction cartDirection) {
        char[] chars = line.toLowerCase().toCharArray();
        boolean originFound = false;
        for (int i = 0; i < chars.length; i++) {
            if (!originFound) {
                do {
                    originFound = originFound || checkCartDirection(cartDirection, chars[i]);
                }
                while (++i < chars.length && IntersectionCharacter.getIntersectionCharacter(chars[i]) != IntersectionCharacter.LEFT_DIRECTION_DELIMITER);
            }
            if (++i < chars.length && originFound) {
                boolean containsCartType = false;
                do {
                    IntersectionCharacter type = IntersectionCharacter.getIntersectionCharacter(chars[i]);
                    IntersectionCharacter state = IntersectionCharacter.ANY_LOAD;
                    if (type != IntersectionCharacter.ANY_CART && type != IntersectionCharacter.MINECART
                            && type != IntersectionCharacter.STORAGE_CART && type != IntersectionCharacter.POWERED_CART) {
                        return null;
                    }
                    if ((i + 1) < chars.length && IntersectionCharacter.getIntersectionCharacter(chars[i + 1]) != IntersectionCharacter.CART_DELIMITER
                            && IntersectionCharacter.getIntersectionCharacter(chars[i + 1]) != IntersectionCharacter.RIGHT_DIRECTION_DELIMITER) {
                        state = IntersectionCharacter.getIntersectionCharacter(chars[++i]);
                    }
                    if (checkCartType(cart, type)) {
                        containsCartType = containsCartType || state == null || checkCartState(cart, state);
                    }
                }
                while (++i < chars.length && IntersectionCharacter.getIntersectionCharacter(chars[i]) != IntersectionCharacter.RIGHT_DIRECTION_DELIMITER);
                if (++i < chars.length && containsCartType) {
                    return IntersectionCharacter.parseDirection(chars[i]);
                }
                if (++i + 1 < chars.length && IntersectionCharacter.getIntersectionCharacter(chars[i]) == IntersectionCharacter.REMAINDER_DELIMITER) {
                    return IntersectionCharacter.parseDirection(chars[++i]);
                }
                if (i + 1 < chars.length && IntersectionCharacter.getIntersectionCharacter(chars[i]) == IntersectionCharacter.STATEMENT_DELIMITER) {
                    continue;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public static boolean checkCartDirection(Direction cartDirection, char character){
        return character == IntersectionCharacter.ANY_DIRECTION.getCharacter() || cartDirection == IntersectionCharacter.parseDirection(character);
    }

    public static boolean checkCartType(Minecart cart, IntersectionCharacter type) {
        switch (type) {
            case ANY_CART:
                return true;
            case MINECART:
                return !(cart instanceof StorageMinecart || cart instanceof PoweredMinecart);
            case POWERED_CART:
                return cart instanceof PoweredMinecart;
            case STORAGE_CART:
                return cart instanceof StorageMinecart;
            default:
                return false;
        }
    }

    public static boolean checkCartState(Minecart cart, IntersectionCharacter state) {
        if (state == IntersectionCharacter.ANY_LOAD) {
            return true;
        } else {
            boolean s = (state == IntersectionCharacter.EMPTY_CART);
            if (cart instanceof PoweredMinecart) {
                return true;
            } else if (cart instanceof StorageMinecart) {
                StorageMinecart storageMinecart = (StorageMinecart) cart;
                return InventoryManager.isEmpty(storageMinecart.getInventory().getContents()) == s;
            } else {
                return (cart.getPassenger() == null) == s;
            }
        }
    }
}
