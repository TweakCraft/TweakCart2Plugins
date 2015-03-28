package net.tweakcraft.tweakcart.cartrouting.listener;

import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import net.tweakcraft.tweakcart.cartrouting.model.ActionType;
import net.tweakcraft.tweakcart.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.Lever;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Created by nick on 25/03/2015.
 */
public class CartRoutingEventListener extends TweakSignEventListener {

    private Plugin plugin;

    public CartRoutingEventListener(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        ActionType type = ActionType.getActionType(event.getSign().getLine(0));

        switch(type){
            case SETDEST:
                event.getMinecart().setMetadata("Destination", new FixedMetadataValue(plugin, event.getSign().getLine(1)));
                break;
            case GETDEST:
                if (event.getMinecart().hasMetadata("Destination") && event.getMinecart().getLocation().getBlock().getType() == Material.DETECTOR_RAIL) {
                    if (event.getMinecart().getMetadata("Destination").get(0).asString().equalsIgnoreCase(event.getSign().getLine(1))) {
                        DetectorRailListener.getInstance().activate(event.getMinecart().getLocation().getBlock().getLocation());
                    }
                }
                break;
        }
    }
}
