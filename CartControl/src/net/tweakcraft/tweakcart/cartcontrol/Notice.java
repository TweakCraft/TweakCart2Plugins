/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.tweakcraft.tweakcart.cartcontrol;

import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.api.event.listeners.TweakSignEventListener;
import org.bukkit.entity.Player;

/**
 *
 * @author lennart
 */
public class Notice extends TweakSignEventListener{
    
    @Override
    public void onSignPass(TweakVehiclePassesSignEvent event) {
        String[] lines = event.getSign().getLines();
        String result = "";
        for(int i = 1; i < lines.length; i++){
            result += lines[i];
        }
        
        if(event.getMinecart().getPassenger() instanceof Player){
            Player p = (Player) event.getMinecart().getPassenger();
            p.sendMessage(result);
        }
    }
}
