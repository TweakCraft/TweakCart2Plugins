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
        for (int i = 0; i < from.length; i++) {
            ItemStack fStack = from[i];
            if (fStack == null || !fStack.getEnchantments().isEmpty()) {
                continue;
            }
            for (int j = 0; j < to.length; j++) {
                ItemStack tStack = to[i];
                if (tStack == null) {
                    tStack = fStack;
                    fStack = null;
                } else if (tStack.getAmount() == 64) {
                    continue;
                } else if (fStack.getTypeId() == tStack.getTypeId() && fStack.getDurability() == tStack.getDurability() && tStack.getEnchantments().isEmpty()) {
                    //And now the magic begins
                    int total = fStack.getAmount() + tStack.getAmount();
                    if (total >= 64) {
                        //TODO: use this value to substract from IntMap.
                        int removed = 64 - tStack.getAmount();
                        map.setInt(tStack.getType(), (byte) tStack.getDurability(), map.getInt(tStack.getType(), (byte) tStack.getDurability()) - removed);
                    }
                }
                to[i] = tStack;
            }
            from[i] = fStack;
        }
        return 0;
    }
}
