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

package net.tweakcraft.tweakcart.elevator;

import org.bukkit.block.Sign;

public class ElevatorParser {
    public static final ElevateDirection poweredDirection = ElevateDirection.DOWN;
    public static final ElevateDirection unpoweredDirection = ElevateDirection.UP;

    public enum ElevateDirection {
        UP,
        DOWN
    }

	public static ElevateDirection parseSign(Sign sign)
	{
		for (String line : sign.getLines()) 
		{
			line = line.toLowerCase();
			if(line.equals("up"))
			{
				return ElevateDirection.UP;
			}
			else if(line.equals("down"))
			{
				return ElevateDirection.DOWN;
			}
		}
		return sign.getBlock().isBlockPowered() ? poweredDirection : unpoweredDirection;
	}
}
