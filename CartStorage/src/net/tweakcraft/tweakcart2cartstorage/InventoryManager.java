package net.tweakcraft.tweakcart2cartstorage;

import net.tweakcraft.tweakcart2cartstorage.model.IntMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Edoxile
 *         Here comes the black magic
 */
public class InventoryManager {

    //returns state of FROM-container {-1=empty,0=space left,1=full}
    public static int moveContainerContents(Inventory iFrom, Inventory iTo, IntMap map) {
        ItemStack[] from = iFrom.getContents();
        ItemStack[] to = iTo.getContents();
        fromLoop:
        for (int i = 0; i < from.length; i++) {
            ItemStack fStack = from[i];
            int maxAmountToMove = map.getInt(fStack.getType(), (byte) fStack.getDurability());
            if (fStack == null || !fStack.getEnchantments().isEmpty() || maxAmountToMove == 0) {
                continue;
            }
            toLoop:
            for (int j = 0; j < to.length; j++) {
                ItemStack tStack = to[i];
                if (tStack == null) {
                    to[i] = fStack;
                    from[i] = null;
                    continue fromLoop;
                } else if (tStack.getAmount() == 64) {
                    continue;
                } else if (fStack.getTypeId() == tStack.getTypeId() && fStack.getDurability() == tStack.getDurability() && tStack.getEnchantments().isEmpty()) {
                    //And now the magic begins
                    //First check if the stackAmount is smaller then the max amount to move
                    if (fStack.getAmount() <= maxAmountToMove) {
                        int total = fStack.getAmount() + tStack.getAmount();
                        if (total > 64) {
                            map.setInt(tStack.getType(), (byte) tStack.getDurability(), map.getInt(tStack.getType(), (byte) tStack.getDurability()) - (64 - tStack.getAmount()));
                            tStack.setAmount(64);
                            fStack.setAmount(total - 64);
                        } else {
                            map.setInt(tStack.getType(), (byte) tStack.getDurability(), map.getInt(tStack.getType(), (byte) tStack.getDurability()) - fStack.getAmount());
                            tStack.setAmount(total);
                            from[i] = null;
                            continue fromLoop;
                        }
                    } else {
                        //Otherwise, run some other code
                        int total = maxAmountToMove + tStack.getAmount();
                        int stableAmount = fStack.getAmount() - maxAmountToMove;
                        if (total > 64) {
                            map.setInt(tStack.getType(), (byte) tStack.getDurability(), map.getInt(tStack.getType(), (byte) tStack.getDurability()) - (64 - tStack.getAmount()));
                            maxAmountToMove -= 64 - tStack.getAmount();
                            tStack.setAmount(64);
                            fStack.setAmount(total - 64 + stableAmount);
                        } else {
                            map.setInt(tStack.getType(), (byte) tStack.getDurability(), map.getInt(tStack.getType(), (byte) tStack.getDurability()) - maxAmountToMove);
                            tStack.setAmount(total);
                            if (stableAmount > 0) {
                                from[i].setAmount(stableAmount);
                            } else {
                                from[i] = null;
                            }
                            continue fromLoop;
                        }
                    }
                } else {
                    continue;
                }
                to[i] = tStack;
            }
            from[i] = fStack;
        }
        //For now, just return 0. Check are to be built in after this is properly tested.
        return 0;
    }
}
