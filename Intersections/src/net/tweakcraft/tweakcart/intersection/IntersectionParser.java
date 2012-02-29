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

package net.tweakcraft.tweakcart.intersection;

import net.tweakcraft.tweakcart.api.parser.DirectionParser;
import net.tweakcraft.tweakcart.intersection.syntax.IntersectionCharacter;
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
        Direction direction = DirectionParser.parseDirection(line);
        int directionCheck = (direction == null) ? ((direction == cartDirection) ? 1 : 0) : -1;
        if (direction == null) {
            return null;
        } else if (direction != cartDirection) {
            return Direction.SELF;
        } else {
            line = DirectionParser.removeDirection(line);
            boolean containsCartType = false;
            boolean isRemainder = false;
            Boolean cartMustBeEmpty = null;
            //So, now that we know that the cart is going in the correct direction, recycle the direction object.
            mainLoop:
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
                    case EMPTY_CART:
                        cartMustBeEmpty = true;
                        break;
                    case FULL_CART:
                        cartMustBeEmpty = false;
                        break;
                    case CART_DELIMITER:
                        if (!containsCartType) {
                            //TODO: Reset variables;
                            continue mainLoop;
                        }
                        if (cartMustBeEmpty == null) {
                            //State doesn't matter
                        } else if (cartMustBeEmpty) {
                            //Check if cart is empty
                            if (cart instanceof StorageMinecart && !InventoryManager.isEmpty(((StorageMinecart) cart).getInventory().getContents())) {
                                //TODO: Reset variables;
                                continue mainLoop;
                            } else if (cart instanceof PoweredMinecart) {
                                //TODO: check if it's possible to check if PoweredMinecart is empty or not.
                                continue mainLoop;
                            } else if (!(cart instanceof PoweredMinecart) && !(cart instanceof StorageMinecart) && cart.getPassenger() != null) {
                                continue mainLoop;
                            }
                            //Send cart to direction
                        } else {
                            if (cart instanceof StorageMinecart && InventoryManager.isEmpty(((StorageMinecart) cart).getInventory().getContents())) {
                                //TODO: Reset variables;
                                continue mainLoop;
                            } else if (cart instanceof PoweredMinecart) {
                                //TODO: check if it's possible to check if PoweredMinecart is empty or not.
                                continue mainLoop;
                            } else if (cart.getPassenger() == null) {
                                continue mainLoop;
                            }
                            return direction;
                            //Send cart to direction         
                            //VehicleUtil.moveCartRelative(cart, cartDirection.getModX() + direction.getModX(), 0, cartDirection.getModZ() + direction.getModZ());
                            //Vector velocity = cart.getVelocity();
                            //cart.setVelocity(new Vector(direction.getModX(), direction.getModY(), direction.getModZ()).multiply(MathUtil.max(velocity.getX(), velocity.getZ())));
                        }
                        break;
                    case DIRECTION_DELIMITER:
                        continue mainLoop;
                    case DIRECTION:
                        direction = IntersectionCharacter.getDirection(character);
                        if (direction == null) {
                            //Something went terribly wrong -_-
                            return null;
                        } else {
                            if(isRemainder){
                                return direction;
                            }
                            continue mainLoop;
                        }
                    case REMAINDER_DELIMITER:
                        isRemainder = true;
                        break;
                    default:
                        //Syntax error
                        return null;
                }
            }
            //Return the direction
            return Direction.SELF;
        }
    }

    //1 = correct direction, 0 = wrong direction, -1 is no direction found
    public static int parseDirection(String line, Direction cartDirection) {
        Direction direction = DirectionParser.parseDirection(line);
        if (direction == null) {
            return -1;
        } else {
            return (direction == cartDirection) ? 1 : 0;
        }
    }
}
