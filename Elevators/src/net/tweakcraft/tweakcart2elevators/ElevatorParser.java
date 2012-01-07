package net.tweakcraft.tweakcart2elevators;

import org.bukkit.block.Sign;
import net.tweakcraft.tweakcart.api.plugin.AbstractParser;

public class ElevatorParser extends AbstractParser{
    public static final ElevateDirection poweredDirection = ElevateDirection.DOWN;
    public static final ElevateDirection unpoweredDirection = ElevateDirection.UP;
    
    public enum ElevateDirection{
        UP,
        DOWN
    }
    
    /**
     * There should be a parser for every key, but this one is pretty basic
     * If the sign is powered, go down, otherwise, go up
     */
    @Override
    public ElevateDirection parseSign(Sign s) {
        return s.getBlock().isBlockPowered() ? poweredDirection : unpoweredDirection;
    }

}
