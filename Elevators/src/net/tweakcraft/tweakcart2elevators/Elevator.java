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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.TweakPluginManager;
import net.tweakcraft.tweakcart.api.TweakCartEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehicleCollidesWithSignEvent;
import net.tweakcraft.tweakcart.api.plugin.AbstractSignPlugin;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.VehicleUtil;
import net.tweakcraft.tweakcart2elevators.ElevatorParser.ElevateDirection;

public class Elevator extends AbstractSignPlugin {
    
    private static final int MAX_HEIGHT = 32;
    private ElevatorParser parser = null;

    
    public Elevator(TweakCart p) {
        super(p);
    }

    @Override
    public void onEnable() {
        plugin.log("Elevators enabled");
        plugin.getPluginManager().registerEvent(this, TweakCartEvent.Sign.VehicleCollidesWithSignEvent, "elevator");
        parser = new ElevatorParser();
    }

    @Override
    public String getPluginName() {
        return "Elevators";
    }
    
    @Override
    public void onSignCollision(TweakVehicleCollidesWithSignEvent event) {
        Minecart mine = event.getMinecart();
        Sign sign = event.getSign();
        Direction heading = event.getDirection();
        ElevateDirection ev = parser.parseSign(sign);
        System.out.println(ev);
        Location newLocation = null;
        switch(ev){
        case UP:
            newLocation = findSign(true, sign, heading);
            break;
        case DOWN:
            newLocation = findSign(false, sign, heading);
            break;
        }
        if(newLocation != null){
            //Een beetje verplaatsen wegens direction, anders komt de cart in de sign te staan, nu blijft de cart dezelfde kant op rijden
            VehicleUtil.moveCart(mine, newLocation.add((double)heading.getModX()*1.5,(double)heading.getModY()*1.5,(double)heading.getModZ()*1.5));
        }
    }

    private Location findSign(boolean isUp, Sign s, Direction heading) {
        Block signBlock = s.getBlock();
        int height = s.getY();
        
        int maxIt = Math.min(isUp? 129 - height : height, MAX_HEIGHT); 
        BlockFace direction = isUp? BlockFace.UP : BlockFace.DOWN;
        
        for(int i = 0; i < maxIt; i++){
            signBlock = signBlock.getRelative(direction);
            if(signBlock.getState() instanceof Sign && VehicleUtil.canSpawn(signBlock.getRelative(heading.getModX(), heading.getModY(), heading.getModZ()))){
                
                return signBlock.getLocation();
            }
        }
        return null;
    }
    


}
