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

import net.tweakcraft.tweakcart.intersection.syntax.DirectionCharacter;
import net.tweakcraft.tweakcart.intersection.syntax.IntersectionCharacter;
import net.tweakcraft.tweakcart.model.Direction;
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
        int direction = parseDirection(line, cartDirection);
        Direction remainder;

        if (direction == -1) {
            return null;
        } else if (direction == 0) {
            return Direction.SELF;
        } else {
            //Chop off direction
            line = line.substring(2);
            //Chop off the remainder statement
            String[] apple = line.split(IntersectionCharacter.REMAINDER_DELIMITER.getCharacter());
            if (apple.length == 1) {
                remainder = cartDirection;
            } else if (apple.length == 2) {
                remainder = DirectionCharacter.getDirection(apple[1]);
                if (remainder == null) {
                    return null;
                }
                line = line.substring(0, line.length() - 1);
            } else {
                return null;
            }

            apple = line.split(IntersectionCharacter.CART_DELIMITER.getCharacter());
            for (String mango : apple) {
                String[] banana = mango.split(IntersectionCharacter.DIRECTION_DELIMITER.getCharacter());
                if (apple.length == 2) {
                    remainder = DirectionCharacter.getDirection(banana[1]);
                    if (remainder == null) {
                        return null;
                    }
                } else {
                    //Syntax error (no to-direction given)
                    return null;
                }

                //0 = no type found, 1 = empty, 2 = full;
                int type;
                boolean cartFound = false;

                for (int i = 0; i < mango.length(); i++) {
                    IntersectionCharacter ic = IntersectionCharacter.getIntersectionCharacter(mango.substring(i, i + 1));
                    switch (ic) {
                        case MINECART:
                            if (!(cart instanceof StorageMinecart) && !(cart instanceof PoweredMinecart)) {
                                cartFound = true;
                            }
                            break;
                        case STORAGE_CART:
                            if (cart instanceof StorageMinecart) {
                                cartFound = true;
                            }
                            break;
                        case POWERED_CART:
                            if (cart instanceof PoweredMinecart) {
                                cartFound = true;
                            }
                            break;
                        case EMPTY_CART:
                            //TODO: think of implementation;
                            break;
                        case FULL_CART:
                            //TODO: think of implementation;
                            break;
                        //TODO: put more delimiters in here;
                        default:
                            return null;
                    }
                }
                if(cartFound){
                    return remainder;
                }
            }
            //Remainder
            return remainder;
        }
    }

    public static int parseDirection(String line, Direction cartDirection) {
        if (line.charAt(1) == DirectionCharacter.DELIMITER.getCharacter().charAt(0)) {
            DirectionCharacter directionCharacter = DirectionCharacter.getDirectionCharacter(line.substring(0, 1));
            if (directionCharacter.getDirection().equals(cartDirection)) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }
}
