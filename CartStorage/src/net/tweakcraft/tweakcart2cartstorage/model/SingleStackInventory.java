package net.tweakcraft.tweakcart2cartstorage.model;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
* Created by IntelliJ IDEA.
 *
 * @author Edoxile
*/
public class SingleStackInventory implements Inventory {
    private ItemStack itemStack;

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName() {
        return "SingleStackInventory";
    }

    @Override
    public ItemStack getItem(int i) {
        return (i == 0) ? itemStack : null;
    }

    @Override
    public void setItem(int i, ItemStack stack) {
        if (i == 0) {
            itemStack = stack;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) {
        HashMap<Integer, ItemStack> leftovers = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack stack = itemStacks[i];
            if (itemStack == null) {
                stack = null;
                itemStack = stack;
            } else {
                if (stack.getTypeId() == itemStack.getTypeId() && stack.getDurability() == itemStack.getDurability() && (stack.getEnchantments().isEmpty() && itemStack.getEnchantments().isEmpty())) {
                    int total = stack.getAmount() + itemStack.getAmount();
                    if (total > 64) {
                        itemStack.setAmount(64);
                        stack.setAmount(total - 64);
                    } else {
                        itemStack.setAmount(64);
                        stack = null;
                    }
                }
            }
            if (stack != null) {
                leftovers.put(i, stack);
            }
        }
        return leftovers;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) {
        HashMap<Integer, ItemStack> leftovers = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack stack = itemStacks[i];
            if (itemStack == null) {
                leftovers.put(i, stack);
            } else {
                if (stack.getTypeId() == itemStack.getTypeId() && stack.getDurability() == itemStack.getDurability() && (stack.getEnchantments().isEmpty() && itemStack.getEnchantments().isEmpty())) {
                    int left = itemStack.getAmount() - stack.getAmount();
                    if (left > 0) {
                        itemStack.setAmount(left);
                        stack = null;
                    } else if (left == 0) {
                        itemStack = null;
                        stack = null;
                    } else {
                        itemStack = null;
                        stack.setAmount(-left);
                    }
                }
                leftovers.put(i, stack);
            }
        }
        return leftovers;
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{itemStack};
    }

    @Override
    public void setContents(ItemStack[] itemStacks) {
        if (itemStacks.length != 1) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            itemStack = itemStacks[0];
        }
    }

    @Override
    public boolean contains(int i) {
        return itemStack.getTypeId() == i;
    }

    @Override
    public boolean contains(Material material) {
        return itemStack.getTypeId() == material.getId();
    }

    @Override
    public boolean contains(ItemStack stack) {
        return itemStack.getTypeId() == stack.getTypeId() && itemStack.getDurability() == stack.getDurability();
    }

    @Override
    public boolean contains(int i, int amount) {
        return itemStack.getTypeId() == i && itemStack.getAmount() >= amount;
    }

    @Override
    public boolean contains(Material material, int amount) {
        return itemStack.getTypeId() == material.getId() && itemStack.getAmount() >= amount;
    }

    @Override
    public boolean contains(ItemStack stack, int amount) {
        return itemStack.getTypeId() == stack.getTypeId() && itemStack.getDurability() == stack.getDurability() && itemStack.getAmount() >= amount;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        //TODO: do we need this? i really don't feel like implementing it, because it forces users to use either CraftBukkit or GlowStone
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        //TODO: do we need this? i really don't feel like implementing it, because it forces users to use either CraftBukkit or GlowStone
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack stack) {
        //TODO: do we need this? i really don't feel like implementing it, because it forces users to use either CraftBukkit or GlowStone
        return null;
    }

    @Override
    public int first(int i) {
        return (itemStack.getTypeId() == i) ? 0 : -1;
    }

    @Override
    public int first(Material material) {
        return (itemStack.getTypeId() == material.getId()) ? 0 : -1;
    }

    @Override
    public int first(ItemStack stack) {
        return (itemStack.getTypeId() == stack.getTypeId() && itemStack.getDurability() == stack.getDurability()) ? 0 : -1;
    }

    @Override
    public int firstEmpty() {
        return (itemStack == null) ? 0 : -1;
    }

    @Override
    public void remove(int i) {
        if (itemStack.getTypeId() == i) {
            itemStack = null;
        }
    }

    @Override
    public void remove(Material material) {
        if (itemStack.getTypeId() == material.getId()) {
            itemStack = null;
        }
    }

    @Override
    public void remove(ItemStack stack) {
        if (itemStack.getTypeId() == stack.getTypeId() && itemStack.getDurability() == stack.getDurability()) {
            itemStack = null;
        }
    }

    @Override
    public void clear(int i) {
        if (i == 0) {
            itemStack = null;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void clear() {
        itemStack = null;
    }
}
