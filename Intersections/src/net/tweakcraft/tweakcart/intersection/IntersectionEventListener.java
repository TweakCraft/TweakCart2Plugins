package net.tweakcraft.tweakcart.intersection;

import net.tweakcraft.tweakcart.api.event.TweakVehicleCollidesWithSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.intersection.parser.IntersectionParser;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.VehicleUtil;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

/**
 * Created by edoxile on 2/26/14.
 */
public class IntersectionEventListener extends TweakSignEventListener {

    @Override
    public void onSignCollision(TweakVehicleCollidesWithSignEvent event) {
        Minecart cart = event.getMinecart();
        Sign sign = event.getSign();
        Direction cartDirection = event.getDirection();
        Direction toGo = null;
        for (String line : sign.getLines()) {
            toGo = IntersectionParser.parseIntersection(line, cart, cartDirection);
            if (toGo != null) {
                break;
            }
        }
        if (toGo != null) {
            double velocity = cart.getVelocity().length();
            Location to = sign.getBlock().getLocation().add(toGo.getModX() * 1.5, 0, toGo.getModZ() * 1.5);
            VehicleUtil.moveCart(cart, to);
            cart.setVelocity((new Vector(toGo.getModX(), toGo.getModY(), toGo.getModZ())).multiply(velocity));
        }
    }
}
