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

import net.tweakcraft.tweakcart.api.model.parser.DirectionParser;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.InventoryManager;
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
        //Returns the direction to go to. null = parse error, BlockFace.SELF is incompatible direction.
        //First check the remainder direction
        Direction remainderDirection = Direction.SELF;
        if (line.length() > 2 && line.charAt(line.length() - 2) == IntersectionCharacter.REMAINDER_DELIMITER.getCharacter()) {
            remainderDirection = IntersectionCharacter.parseDirection(line.charAt(line.length() - 1));
            line = line.substring(0, line.length() - 2);
        }

        line += "" + IntersectionCharacter.STATEMENT_DELIMITER;
        Direction direction = DirectionParser.parseDirection(line);

        if (direction == null) {
            return null;
        } else if (direction != cartDirection) {
            return Direction.SELF;
        } else {
            line = DirectionParser.removeDirection(line);
            boolean containsCartType = false;
            Boolean cartMustBeEmpty = null;
            //So, now that we know that the cart is going in the correct direction, recycle the direction object.
            //It'll return the direction the cart has to go to
            for (char character : line.toCharArray()) {
                IntersectionCharacter intersectionCharacter = IntersectionCharacter.getIntersectionCharacter(character);
                switch (intersectionCharacter) {
                    case MINECART:
                        containsCartType = containsCartType || (!(cart instanceof PoweredMinecart) && !(cart instanceof StorageMinecart));
                        break;
                    case POWERED_CART:
                        containsCartType = containsCartType || (cart instanceof PoweredMinecart);
                        break;
                    case STORAGE_CART:
                        containsCartType = containsCartType || (cart instanceof StorageMinecart);
                        break;
                    case ANY_CART:
                        containsCartType = true;
                        break;
                    case EMPTY_CART:
                        cartMustBeEmpty = true;
                        break;
                    case FULL_CART:
                        cartMustBeEmpty = false;
                        break;
                    case STATEMENT_DELIMITER:
                        if (!containsCartType) {
                            cartMustBeEmpty = null;
                            break;
                        }
                        if (cartMustBeEmpty == null) {
                            return direction;
                        } else if (cartMustBeEmpty) {
                            if (cart instanceof StorageMinecart && !InventoryManager.isEmpty(((StorageMinecart) cart).getInventory().getContents())) {
                                cartMustBeEmpty = null;
                                containsCartType = false;
                            } else if (!(cart instanceof PoweredMinecart) && !(cart instanceof StorageMinecart) && cart.getPassenger() != null) {
                                cartMustBeEmpty = null;
                                containsCartType = false;
                            } else {
                                return direction;
                            }
                        } else {
                            if (cart instanceof StorageMinecart && InventoryManager.isEmpty(((StorageMinecart) cart).getInventory().getContents())) {
                                cartMustBeEmpty = null;
                                containsCartType = false;
                            } else if (cart.getPassenger() == null) {
                                cartMustBeEmpty = null;
                                containsCartType = false;
                            } else {
                                return direction;
                            }
                        }
                        break;
                    case DIRECTION_DELIMITER:
                        break;
                    case DIRECTION:
                        direction = IntersectionCharacter.parseDirection(character);
                        if (direction == null) {
                            return null;
                        } else {
                            break;
                        }
                    default:
                        return null;
                }
            }
            return direction;
        }
    }

    //1 = correct direction, 0 = wrong direction, -1 is no direction found
    @Deprecated
    public static int parseDirection(String line, Direction cartDirection) {
        Direction direction = DirectionParser.parseDirection(line);
        return (direction == null) ? ((direction == cartDirection) ? 1 : 0) : -1;
    }
}
