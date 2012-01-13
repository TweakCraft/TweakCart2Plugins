package net.tweakcraft.tweakcart2intersections;

import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart2intersections.syntax.DirectionCharacter;
import net.tweakcraft.tweakcart2intersections.syntax.IntersectionCharacter;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class TweakIntersection {
    public Direction parseIntersection(String line, Minecart cart, Direction cartDirection, boolean isFull) {
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
                            //TODO: checks etc
                            if (!(cart instanceof StorageMinecart) && !(cart instanceof PoweredMinecart)) {
                                cartFound = true;
                                break;
                            } else {
                                break;
                            }
                        case STORAGE_CART:
                            if (cart instanceof StorageMinecart) {
                                cartFound = true;
                                break;
                            } else {
                                break;
                            }
                        case POWERED_CART:
                            if (cart instanceof PoweredMinecart) {
                                cartFound = true;
                                break;
                            } else {
                                break;
                            }
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
