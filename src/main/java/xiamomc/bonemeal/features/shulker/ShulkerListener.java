package xiamomc.bonemeal.features.shulker;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.kyori.adventure.key.Key;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import xiamomc.bonemeal.XiaMoExperience;

import java.util.Map;

public class ShulkerListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        var item = e.getItem();
        if (item == null)
            return;

        if (!e.getAction().isRightClick() || e.getClickedBlock() != null) return;

        if (item.getItemMeta() instanceof BlockStateMeta blockStateMeta
            && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox)
        {
            var inv = Bukkit.createInventory(shulkerBox.getInventory().getHolder(), InventoryType.SHULKER_BOX);
            inv.setContents(shulkerBox.getInventory().getContents());

            itemStackMap.put(inv, ItemRecord.of(item));

            var player = e.getPlayer();

            try
            {
                player.openInventory(inv);
            }
            catch (Throwable t)
            {
                logger.info("Failed to open inventory for player '%s': '%s'".formatted(player.getName(), t.getMessage()));

                itemStackMap.remove(inv);
            }

            player.playSound(player, Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);

            var hand = e.getHand();
            if (hand != null)
                player.swingHand(hand);
        }
    }

    private final Logger logger = XiaMoExperience.getInstance().getSLF4JLogger();

    private final Map<Inventory, ItemRecord> itemStackMap = new Object2ObjectArrayMap<>();

    private static class ItemRecord
    {
        private final ItemStack bindingStack;

        public ItemRecord(ItemStack bindingStack)
        {
            this.bindingStack = bindingStack;
        }

        private ItemStack bindingCopy;

        public ItemStack getAlternative()
        {
            if (bindingCopy != null)
                return bindingCopy;

            bindingCopy = new ItemStack(bindingStack);

            return bindingCopy;
        }

        public static final ItemRecord of(ItemStack bindingStack)
        {
            return new ItemRecord(bindingStack);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e)
    {
        var inv = e.getInventory();
        if (inv.getType() != InventoryType.SHULKER_BOX) return;

        this.onInvClose(inv);

        var player = e.getPlayer();
        player.playSound(net.kyori.adventure.sound.Sound.sound(
                Key.key("block.shulker_box.close"),
                net.kyori.adventure.sound.Sound.Source.PLAYER, 1, 1));
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e)
    {
        var item = e.getItemDrop();

        var stack = item.getItemStack();

        var valuesContains = itemStackMap.values().stream().anyMatch(i -> i.getAlternative().isSimilar(stack));

        if (valuesContains)
            e.setCancelled(true);
    }

    private void onInvClose(Inventory closedInventory)
    {
        var matchedRec = itemStackMap.remove(closedInventory);

        if (matchedRec == null) return;

        var match = matchedRec.bindingStack;

        if (!(match.getItemMeta() instanceof BlockStateMeta state))
            return;

        if (!(state.getBlockState() instanceof ShulkerBox shulkerBox))
            return;

        shulkerBox.getInventory().setContents(closedInventory.getContents());
        state.setBlockState(shulkerBox);
        match.setItemMeta(state);
    }

    public void onDisable()
    {
        //不关闭打开的背包可能会导致物品复制
        Bukkit.getOnlinePlayers().forEach( p ->
        {
            var invView = p.getOpenInventory();

            this.onInvClose(invView.getTopInventory());
            this.onInvClose(invView.getBottomInventory());

            p.closeInventory();
        });
    }
}
