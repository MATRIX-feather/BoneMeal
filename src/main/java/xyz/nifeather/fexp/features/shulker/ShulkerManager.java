package xyz.nifeather.fexp.features.shulker;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FeatherExperience;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ShulkerManager
{
    private final Logger logger = FeatherExperience.getInstance().getSLF4JLogger();

    public static class OpenMeta
    {
        private final Player bindingPlayer;
        private final ItemStack stackCopy;
        private final int slot;

        public OpenMeta(Player player, ItemStack stackCopy, int slot)
        {
            this.bindingPlayer = player;
            this.slot = slot;
            this.stackCopy = stackCopy;
        }

        public Player player()
        {
            return bindingPlayer;
        }

        public ItemStack stack()
        {
            return stackCopy;
        }

        public int slot()
        {
            return slot;
        }
    }

    private final Map<Inventory, OpenMeta> playerInventoryMap = new Object2ObjectArrayMap<>();

    @Nullable
    public Map.Entry<Inventory, OpenMeta> getPlayerEntryMeta(Player player)
    {
        return playerInventoryMap.entrySet()
                .stream().filter(set -> set.getValue().player() == player)
                .findFirst().orElse(null);
    }

    public boolean isManagedInventory(Inventory inventory)
    {
        return playerInventoryMap.containsKey(inventory);
    }

    @Nullable
    public OpenMeta getOpenMeta(Inventory inventory)
    {
        return playerInventoryMap.getOrDefault(inventory, null);
    }

    public boolean openingBox(Player player)
    {
        return getPlayerEntryMeta(player) != null;
    }

    /**
     *
     * @param itemStack
     * @param player
     * @param slot
     * @return 操作是否成功
     */
    public boolean tryOpenBox(ItemStack itemStack, Player player, int slot)
    {
        if (!MaterialUtils.isShulkerBox(itemStack.getType()))
            return false;

        if (!player.hasPermission(CommonPermissions.shulkerBox))
            return false;

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta))
        {
            logger.warn("(%s) ItemMeta is not a BlockStateMeta: ".formatted(player.getName()));
            return false;
        }

        if (!(blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox))
        {
            logger.warn("(%s) BlockState is not a ShulkerBox".formatted(player.getName()));
            return false;
        }

        if (getPlayerEntryMeta(player) != null)
        {
            logger.error("Already opened another shulker box!");
            return false;
        }

        var shulkerName = itemStack.getItemMeta().displayName() != null
                ? itemStack.getItemMeta().displayName()
                : Component.translatable(Objects.requireNonNull(Material.SHULKER_BOX.getItemTranslationKey(), "?"));

        // Using `shulkerBox.getInventory()` directly will cause Folia to error
        var inventory = Bukkit.createInventory(
                shulkerBox.getInventory().getHolder(),
                InventoryType.SHULKER_BOX,
                Objects.requireNonNull(shulkerName, "???"));

        inventory.setContents(shulkerBox.getInventory().getContents());

        try
        {
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            player.openInventory(inventory);
        }
        catch (Throwable t)
        {
            logger.error("Unable to open shulker box for player " + player.getName());
            return false;
        }

        var itemCopy = new ItemStack(itemStack);

        playerInventoryMap.put(inventory, new OpenMeta(player, itemCopy, slot));

        player.getWorld().playSound(player, Sound.BLOCK_SHULKER_BOX_OPEN, 0.7f, 1);

        return true;
    }

    public void closeBox(Player player)
    {
        var currentInv = player.getOpenInventory().getTopInventory();

        var openMeta = playerInventoryMap.remove(currentInv);

        if (openMeta == null) return;

        //关闭此Inventory
        player.closeInventory();

        var atomicItem = new AtomicReference<ItemStack>(null);

        //先获取特定槽位的物品
        var itemFromSlot = player.getInventory().getItem(openMeta.slot);

        if (openMeta.stackCopy.equals(itemFromSlot))
        {
            atomicItem.set(itemFromSlot);
        }
        else
        {
            player.getInventory().forEach(i ->
            {
                if (openMeta.stackCopy.equals(i))
                    atomicItem.set(i);
            });
        }

        var boxItem = atomicItem.get();

        if (boxItem == null)
        {
            logger.error("- x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x -");
            logger.error("BINDING STACK DISAPPEARED FROM PLAYER '" + player.getName() + "'!");
            logger.error("THE CONTENT OF THE BOX IS NOT SAVED, A DUPE GLITCH MAY OCCUR!");
            logger.error("- x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x - x -");
            return;
        }

        var meta = (BlockStateMeta) boxItem.getItemMeta();
        var shulker = (ShulkerBox) meta.getBlockState();

        shulker.getInventory().setContents(currentInv.getContents());
        meta.setBlockState(shulker);
        boxItem.setItemMeta(meta);

        player.getWorld().playSound(player, Sound.BLOCK_SHULKER_BOX_CLOSE, 0.7f, 1);
    }
}
