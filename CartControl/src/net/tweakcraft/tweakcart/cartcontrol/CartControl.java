/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.tweakcraft.tweakcart.cartcontrol;

import net.tweakcraft.tweakcart.api.model.TweakCartEvent;
import net.tweakcraft.tweakcart.api.model.TweakCartPlugin;
import net.tweakcraft.tweakcart.util.TweakPluginManager;

/**
 *
 * @author lennart
 */
public class CartControl extends TweakCartPlugin{

    @Override
    public String getPluginName() {
        return "CartControl";
    }

    @Override
    public void registerEvents(TweakPluginManager pluginManager) {
        /*
         * Speed sign, eg
         * *************** *
         *  speed control
         *     110%@16
         * *************** *
         * Will increment speed by 110% and hold that speed for 16 blocks
         */
        SpeedControl speedInstance = new SpeedControl(this);
        pluginManager.registerEvent(speedInstance, TweakCartEvent.Sign.VehiclePassesSignEvent, "speed control");
        pluginManager.registerEvent(speedInstance, TweakCartEvent.Block.VehicleBlockChangeEvent);
        
        Ejector ejectorInstance = new Ejector();
        pluginManager.registerEvent(ejectorInstance, TweakCartEvent.Sign.VehiclePassesSignEvent, "ejector");
        
        DeadStop deadStopInstance = new DeadStop();
        pluginManager.registerEvent(deadStopInstance, TweakCartEvent.Sign.VehiclePassesSignEvent, "stop");
        
        Notice noticeInstance = new Notice();
        pluginManager.registerEvent(noticeInstance, TweakCartEvent.Sign.VehiclePassesSignEvent, "notice");
    }    

    @Override
    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
    }    
}
