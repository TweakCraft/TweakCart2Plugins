package net.tweakcraft.tweakcart.cartcontrol;

/**
 * Directive for a minecart that is under speedcontrol
 * Keeps track of wanted velocity and amount of blocks still remaining
 */
public class SpeedDirective {
    private final double speed;
    private int blocks;
    
    public SpeedDirective(final double speed, int blocks){
        this.speed = speed;
        this.blocks = blocks;
    }
    
    public double getSpeed(){
        return this.speed;
    }
    
    public int getBlocks(){
        return this.blocks;
    }
    
    public void decrBlocks(){
        this.blocks--;
    }
}
