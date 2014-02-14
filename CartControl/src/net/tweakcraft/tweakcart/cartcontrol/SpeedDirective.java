package net.tweakcraft.tweakcart.cartcontrol;

import org.bukkit.util.Vector;

/**
 * Directive for a minecart that is under speedcontrol
 * Keeps track of wanted velocity and amount of blocks still remaining
 */
public class SpeedDirective {
    private final Vector heading;
    private int blocks;
    
    public SpeedDirective(final Vector heading, int blocks){
        this.heading = heading;
        this.blocks = blocks;
    }
    
    public Vector getHeading(){
        return this.heading;
    }
    
    public int getBlocks(){
        return this.blocks;
    }
    
    public void decrBlocks(){
        this.blocks--;
    }
}
