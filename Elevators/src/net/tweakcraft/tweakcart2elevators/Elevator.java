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
        TweakPluginManager.getInstance().registerEvent(this, TweakCartEvent.Sign.VehicleCollidesWithSignEvent, "elevator");
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
        
        Location newLocation = null;
        switch(ev){
        case UP:
            newLocation = findSign(true, sign);
            break;
        case DOWN:
            newLocation = findSign(false, sign);
            break;
        }
        if(newLocation != null){
            //Een beetje verplaatsen wegens direction, anders komt de cart in de sign te staan, nu blijft de cart dezelfde kant op rijden
            VehicleUtil.moveCart(mine, newLocation.add((double)heading.getModX(),(double)heading.getModY(),(double)heading.getModZ()));
        }
    }

    private Location findSign(boolean isUp, Sign s) {
        Block signBlock = s.getBlock();
        int height = s.getY();
        int maxIt = Math.min(isUp? 129 - height : height, MAX_HEIGHT); 
        BlockFace direction = isUp? BlockFace.UP : BlockFace.DOWN;
        
        for(int i = 0; i < maxIt; i++){
            signBlock = signBlock.getRelative(direction);
            if(signBlock instanceof Sign){
                return signBlock.getLocation();
            }
        }
        return null;
    }
    


}
