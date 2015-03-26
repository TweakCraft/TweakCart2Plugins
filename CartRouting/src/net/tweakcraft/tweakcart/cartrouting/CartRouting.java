package net.tweakcraft.tweakcart.cartrouting;

import net.tweakcraft.tweakcart.TweakCart;
import net.tweakcraft.tweakcart.api.model.TweakCartEvent;
import net.tweakcraft.tweakcart.api.model.TweakCartPlugin;
import net.tweakcraft.tweakcart.cartrouting.listener.CartRoutingEventListener;
import net.tweakcraft.tweakcart.cartrouting.listener.DetectorRailListener;
import net.tweakcraft.tweakcart.util.TweakPluginManager;

/**
 * Created by nick on 25/03/2015.
 */
public class CartRouting extends TweakCartPlugin {

    @Override
    public String getPluginName() { return "CartRouting"; }

    @Override
    public void registerEvents(TweakPluginManager pluginManager) {
        pluginManager.registerEvent(DetectorRailListener.getInstance(), TweakCartEvent.Block.VehicleBlockRedstoneEvent);

        pluginManager.registerEvent(new CartRoutingEventListener(this), TweakCartEvent.Sign.VehiclePassesSignEvent, "set destination", "get destination");
    }

}
