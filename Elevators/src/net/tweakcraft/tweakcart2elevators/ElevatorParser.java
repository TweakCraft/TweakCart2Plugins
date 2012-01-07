package net.tweakcraft.tweakcart2elevators;

import net.tweakcraft.tweakcart.api.plugin.AbstractParser;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.Map;

public class ElevatorParser extends AbstractParser{
    public enum ElevateDirection{
        UP,
        DOWN,
        INVALID
    }

    public enum PoweredState{
        POWERED,
        UNPOWERED
    }

    @Override
    public ElevateDirection parseSign(Sign s) {
        boolean powered = s.getBlock().isBlockPowered();

        PoweredState ps = powered ? PoweredState.POWERED : PoweredState.UNPOWERED;
        for(String line: s.getLines()){
            if(line.toLowerCase().contains("elevator")) continue;
            Map<PoweredState, ElevateDirection> map;
            if((map = keyValue(line)).containsKey(ps)){
                return map.get(ps);
            }
            else{
                return powered? ElevateDirection.DOWN : ElevateDirection.UP;
            }
        }
        return ElevateDirection.INVALID;
    }

    private Map<PoweredState, ElevateDirection> keyValue(String line) {
        String[] split;
        Map<PoweredState, ElevateDirection> map = new HashMap<PoweredState, ElevateDirection>();
        if((split = line.split(":")).length == 2){
            String key = split[0];
            String value = split[1];

            switch(key.charAt(0)){
                case 'p':
                    getElevateDirection(map, PoweredState.POWERED, value);
                case 'u':
                    getElevateDirection(map, PoweredState.POWERED, value);
            }
        }
        return map;
    }

    private void getElevateDirection(Map<PoweredState,ElevateDirection> map, PoweredState state, String value){
        if(value.equalsIgnoreCase("up")) map.put(state, ElevateDirection.UP);
        else if(value.equalsIgnoreCase("down")) map.put(state, ElevateDirection.DOWN);
        else map.put(state, ElevateDirection.INVALID);
    }

}