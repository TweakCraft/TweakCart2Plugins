package net.tweakcraft.tweakcart.cartrouting.listener;

import net.tweakcraft.tweakcart.api.event.TweakVehicleBlockChangeEvent;
import net.tweakcraft.tweakcart.api.event.TweakVehicleBlockRedstoneEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakBlockEventListener;
import net.tweakcraft.tweakcart.cartrouting.HelperClass;
import net.tweakcraft.tweakcart.cartrouting.model.ActionType;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Listener;
import org.bukkit.material.DetectorRail;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick on 26/03/2015.
 */
public class DetectorRailListener extends TweakBlockEventListener implements Listener {

    private static DetectorRailListener instance = null;

    private Set<Location> activated;

    protected DetectorRailListener(){
        activated = new HashSet<Location>();
    }

    public static DetectorRailListener getInstance(){
        if(instance == null)
            instance = new DetectorRailListener();

        return instance;
    }

    public void activate(Location location){
        activated.add(location);
    }

    @Override
    public void onVehicleBlockRedstoneEvent(TweakVehicleBlockRedstoneEvent event) {
        if(event.getNewCurrent() == 0)
            activated.remove(event.getBlock().getLocation());

        Sign sign = HelperClass.findSign(event.getBlock(), ActionType.GETDEST.getText());

        if(event.getBlock().getType() == Material.DETECTOR_RAIL && !activated.contains(event.getBlock().getLocation()) && sign != null) {
            event.setNewCurrent(0);
        }
    }
}
