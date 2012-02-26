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
import net.tweakcraft.tweakcart.model.Direction;
import org.bukkit.entity.Minecart;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class IntersectionParser {
    public static Direction parseIntersection(String line, Minecart cart, Direction cartDirection, boolean isFull) {
        //Returns the direction to go to. null = parse error, BlockFace.SELF is incompatible direction.
        int direction = parseDirection(line, cartDirection);

        Direction remainder = DirectionParser.parseDirection(line);
        //Remainder
        return remainder;
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
