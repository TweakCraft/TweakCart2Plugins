package net.tweakcraft.tweakcart.cartrouting;

import net.tweakcraft.tweakcart.cartrouting.model.ActionType;
import net.tweakcraft.tweakcart.util.BlockUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Created by nick on 26/03/2015.
 */
public class HelperClass {

    public static Sign findSign(Block block, String firstLine){
        int[][] locations = { {1,0}, {0,1}, {-1,0}, {0,-1} }; /* A diff in every direction from the origin */
        System.out.println("FindSign!");
        System.out.println(block.getType().name());

        for(int[] diff : locations) {
            if (BlockUtil.isSign(block.getRelative(diff[0], 0, diff[1]))){
                Sign sign = (Sign) block.getRelative(diff[0], 0, diff[1]).getState();
                if(sign.getLine(0).equalsIgnoreCase(ActionType.GETDEST.getText()))
                    return sign;
                else return null;
            } else
                System.out.println(block.getRelative(diff[0], 0, diff[1]).getType().name());
        }
        System.out.println("End FindSign!");
        return null;
    }

}
