package net.tweakcraft.tweakcart.cartstorage.test;

import java.util.ArrayList;

import net.minecraft.server.EntityMinecart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import net.tweakcraft.tweakcart.api.event.TweakVehiclePassesSignEvent;
import net.tweakcraft.tweakcart.cartstorage.CartStorage;
import net.tweakcraft.tweakcart.model.Direction;
import net.tweakcraft.tweakcart.model.IntMap;
import net.tweakcraft.tweakcart.test.TweakCartInventoryTest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftStorageMinecart;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;

public class TestCartStorage {
    private CraftSign sign;
    private CraftChest chest;
    private CraftStorageMinecart storageCart;
    private CartStorage.CartStorageEventListener eventListener;

    public class MalformedInvContentException extends Exception {
        private String error;

        public MalformedInvContentException(String error) {
            this.error = error;
        }

        public String getError() {
            return this.error;
        }
    }

    public TestCartStorage(CraftServer craftServer, CartStorage.CartStorageEventListener eventListener) {
        MinecraftServer server = craftServer.getServer();
        WorldServer world = server.getWorldServer(0);
        CraftWorld craftWorld = world.getWorld();

        EntityMinecart cart = new EntityMinecart(world, 1d, 1d, 1d, 0);
        storageCart = new CraftStorageMinecart(craftServer, cart);

        /*
           * init block
           */
        craftWorld.getBlockAt(1, 1, 1).setType(Material.SIGN_POST);
        craftWorld.getBlockAt(1, 2, 1).setType(Material.CHEST);

        /*
           * sign
           */
        Block signBlock = craftWorld.getBlockAt(1, 1, 1);
        sign = new CraftSign(signBlock);

        /*
           * chest
           */
        Block chestBlock = craftWorld.getBlockAt(1, 2, 1);
        chest = new CraftChest(chestBlock);

        this.eventListener = eventListener;
    }

    public void testAll() 
	{
        try 
		{
            System.out.println("== TestResult ==== " + this.testCase(
                "collect items|all items",
                "7:64|5:64||7:64;5:64"
            ));
			this.testCase(
                "collect items|1-10,!2,!3|35;15@10",
                "1:64|1:64|1:64|1:64"
            );
            this.testCase(
                "collect items|92,281,282,295-|297,319,320,322|Food",
                "1:64|1:64|1:64|1:64"
            );
            this.testCase(
                "collect items|all items@10",
                "1:64|1:64|1:64|1:64"
            );
			this.testCase(
                "collect items|all items@10:20",
                "1:64|1:64|1:64|1:64"
            );
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|20@20||b20 glas per",
                "20:64||20:44|20:20"
            ));
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|34-36",
                "35:64|5:64||35:64;5:64"
            ));
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|35",
                "35:64|5:64||35:64;5:64"
            ));
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|5",
                "5:64|35:64||35:64;5:64"
            ));
 			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|35;4",
                "35:64|5:64||35:64;5:64"
            ));
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|7@32",
                "7:64|5:64|7:32|7:32;5:64"
            ));
            System.out.println("== TestResult ==== " + this.testCase(
                "collect items|1-10|!5",
                "7:64;5:64;3:32||5:64|7:64;3:32"
            ));
			System.out.println("== TestResult ==== " + this.testCase(
                "collect items|all items",
                "342:64;2265:64|||342:64;2265:64"
            ));
		}
		catch (MalformedInvContentException MIE)
		{
			System.out.println("Malformed case:" + MIE.getError());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }

    public boolean testCase(String caseSign, String... content) throws Exception {
        String[] invs;
        ItemStack[][] invStacks = new ItemStack[4][];
        if (content.length == 1) {
            invs = content[0].split("\\|");
        } else {
            invs = content;
        }
        if (invs.length != 4) {
            throw new MalformedInvContentException("need 4 invs");
        }
        for (int s = 0; s < 4; s++) {
            ArrayList<ItemStack> list = new ArrayList<ItemStack>();
            if (!invs[s].isEmpty()) {
                String[] stacks = invs[s].split("\\;");
                for (int x = 0; x < stacks.length; x++) {
                    String[] stackVal = stacks[x].split("\\:");
                    if (stackVal.length != 2) {
                        throw new MalformedInvContentException("some stacks hasn't got an amount");
                    }
                    int amount = Integer.parseInt(stackVal[1]);

                    while (amount > 0) {
                        if (amount > 64) {
                            list.add(new ItemStack(Integer.parseInt(stackVal[0]), 64));
                        } else {
                            list.add(new ItemStack(Integer.parseInt(stackVal[0]), amount));

                        }
                        amount -= 64;
                    }
                }
            }
            invStacks[s] = list.toArray(new ItemStack[list.size()]);
            if (invStacks[s] == null) {
                invStacks[s] = new ItemStack[0];
            }
        }
		System.out.println(caseSign);
        return this.testCase(
            caseSign,
            invStacks[0],
            invStacks[1],
            invStacks[2],
            invStacks[3]
        );
    }

    public boolean testCase(
        String signContent,
        ItemStack[] cartInv,
        ItemStack[] chestInv,
        ItemStack[] resCartInv,
        ItemStack[] resChestInv) {
        String[] signLines = signContent.split("\\|");
        for (int l = 0; l < 4; l++) {
            sign.setLine(l, (l < signLines.length) ? signLines[l] : "");
        }
        storageCart.getInventory().clear();
        storageCart.getInventory().addItem(cartInv);
        chest.getInventory().clear();
        chest.getInventory().addItem(chestInv);

        TweakVehiclePassesSignEvent event = new TweakVehiclePassesSignEvent(
            (Minecart) storageCart,
            Direction.NORTH,
            sign,
            "collect items");

        eventListener.onSignPass(event);

        ItemStack[] cartStacks = storageCart.getInventory().getContents();
        ItemStack[] chestStacks = chest.getInventory().getContents();

        TweakCartInventoryTest invTest = new TweakCartInventoryTest();
		System.out.println("Cart-predicted");
		invTest.printFormatted(resCartInv);
		System.out.println("Cart-actual");
		invTest.printFormatted(cartStacks);
		System.out.println("chest-predicted");
		invTest.printFormatted(resChestInv);
 		System.out.println("chest-actual");
		invTest.printFormatted(chestStacks);
       return (invTest.compareInventories(cartStacks, resCartInv) && invTest.compareInventories(chestStacks, resChestInv));
    }
}