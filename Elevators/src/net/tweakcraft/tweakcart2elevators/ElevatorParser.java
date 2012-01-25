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

package net.tweakcraft.tweakcart2elevators;

import org.bukkit.block.Sign;
import net.tweakcraft.tweakcart.api.plugin.AbstractParser;

public class ElevatorParser extends AbstractParser{
    public static final ElevateDirection poweredDirection = ElevateDirection.DOWN;
    public static final ElevateDirection unpoweredDirection = ElevateDirection.UP;
    
    public enum ElevateDirection{
        UP,
        DOWN
    }
    
    /**
     * There should be a parser for every key, but this one is pretty basic
     * If the sign is powered, go down, otherwise, go up
     */
    @Override
    public ElevateDirection parseSign(Sign s) {
        return s.getBlock().isBlockPowered() ? poweredDirection : unpoweredDirection;
    }

}
