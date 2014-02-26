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

import net.tweakcraft.tweakcart.api.event.TweakVehicleCollidesWithSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.VehicleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;

public class ElevatorEventListener extends TweakSignEventListener {

    private static final int MAX_HEIGHT = 32;

    @Override
    public void onSignCollision(TweakVehicleCollidesWithSignEvent event) {
        Minecart mine = event.getMinecart();
        Sign sign = event.getSign();
        Direction heading = event.getDirection();
        ElevatorParser.ElevateDirection ev = ElevatorParser.parseSign(sign);
        Location newLocation = null;
        switch (ev) {
            case UP:
                newLocation = findSign(true, sign, heading);
                break;
            case DOWN:
                newLocation = findSign(false, sign, heading);
                break;
        }
        if (newLocation != null) {
            //Move the cart a bit (in the direction it's going) so it won't get stuck in a sign
            VehicleUtil.moveCart(mine, newLocation.add((double) heading.getModX() * 1.5, (double) heading.getModY() * 1.5, (double) heading.getModZ() * 1.5));
        }
    }

    private Location findSign(boolean isUp, Sign s, Direction heading) {
        Block signBlock = s.getBlock();
        int height = s.getY();

        int maxIt = Math.min(isUp ? (s.getWorld().getMaxHeight() + 1) - height : height, MAX_HEIGHT);
        BlockFace direction = isUp ? BlockFace.UP : BlockFace.DOWN;

        for (int i = 0; i < maxIt; i++) {
            signBlock = signBlock.getRelative(direction);
            if (signBlock.getState() instanceof Sign && VehicleUtil.isRail(signBlock.getRelative(heading.getModX(), heading.getModY(), heading.getModZ()))) {
                return signBlock.getLocation();
            }
        }
        return null;
    }


}
