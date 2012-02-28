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
    public static Direction parseIntersection(String line, Minecart cart, Direction cartDirection, boolean isFull) {
        //Returns the direction to go to. null = parse error, BlockFace.SELF is incompatible direction.
        Direction direction = DirectionParser.parseDirection(line);
        int directionCheck = (direction == null) ? ((direction == cartDirection) ? 1 : 0) : -1;
        if (direction == null) {
            return Direction.SELF;
        } else if (direction != cartDirection) {
            return null;
        } else {
            line = DirectionParser.removeDirection(line);
            boolean containsCartType = false;
            Boolean cartMustBeEmpty = null;
            //So, now that we know that the cart is going in the correct direction, recycle the direction object.
            mainLoop:for(char character : line.toCharArray()) {
                IntersectionCharacter intersectionCharacter = IntersectionCharacter.getIntersectionCharacter(character);
                switch (intersectionCharacter){
                    case MINECART:
                        containsCartType = containsCartType || (!(cart instanceof PoweredMinecart) && !(cart instanceof StorageMinecart));
                        break;
                    case POWERED_CART:
                        containsCartType = containsCartType || (cart instanceof PoweredMinecart);
                        break;
                    case STORAGE_CART:
                        containsCartType = containsCartType || (cart instanceof StorageMinecart);
                        break;
                    case EMPTY_CART:
                        cartMustBeEmpty = true;
                        break;
                    case FULL_CART:
                        cartMustBeEmpty = false;
                        break;
                    case CART_DELIMITER:
                        if(!containsCartType){
                            //Reset variables;
                            continue mainLoop;
                        }
                        if(cartMustBeEmpty == null){
                            //State doesn't matter
                        } else if(cartMustBeEmpty){
                            //Check if cart is empty
                            if(cart instanceof StorageMinecart && !InventoryManager.isEmpty(((StorageMinecart) cart).getInventory().getContents())){
                                
                            }
                        }
                        break;
                    default:
                        return null;
                }
            }
            //Return the direction
            return direction;
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
