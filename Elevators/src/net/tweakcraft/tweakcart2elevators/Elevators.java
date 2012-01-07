package net.tweakcraft.tweakcart2elevators;

import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.TweakPluginManager;
import net.tweakcraft.tweakcart.api.TweakCartEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehicleCollidesWithSignEvent;
import net.tweakcraft.tweakcart.api.plugin.AbstractSignPlugin;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.VehicleUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;

public class Elevators extends AbstractSignPlugin {
    private static final int MAX_HEIGHT = 32;
    private ElevatorParser parser = null;


    public Elevators(TweakCart p) {
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
        ElevatorParser.ElevateDirection ev = parser.parseSign(sign);

        Location newLocation = null;
        switch(ev){
            case UP:
                newLocation = findSign(true, sign);
                break;
            case DOWN:
                newLocation = findSign(false, sign);
                break;
            case INVALID:
                //Error, do something, or just stand here and watch?
                //yes stading and watching it is;
                return;
        }
        if(newLocation != null){
            //Een beetje verplaatsen wegens direction;
            //TODO: iets met private
            VehicleUtil.moveCart(mine, newLocation.add((double)heading.getModX(),(double)heading.getModY(),(double)heading.getModZ()));
        }
    }

    private Location findSign(boolean isUp, Sign s) {
        Block signBlock = s.getBlock();
        int i = 0;
        int height = s.getY();
        int maxIt = Math.min(isUp? 129 - height : height, MAX_HEIGHT); //Potential to break everything, and i like it that way
        BlockFace direction = isUp? BlockFace.UP : BlockFace.DOWN;

        while(i < maxIt){
            signBlock = signBlock.getRelative(direction);
            if(signBlock instanceof Sign){
                return signBlock.getLocation();
            }
            i++;
        }
        return null;
    }



}