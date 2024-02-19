package xyz.nifeather.fexp.features.shulker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import xyz.nifeather.fexp.FeatherExperience;

import java.util.Objects;

public class ShulkerListener implements Listener
{
    private final ShulkerManager shulkerManager = new ShulkerManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        var item = e.getItem();
        if (item == null)
            return;

        if (!e.getAction().isRightClick() || e.getClickedBlock() != null) return;

        var player = e.getPlayer();

        //如果打开了别的盒子，那么不要处理
        if (shulkerManager.openingBox(player))
            return;

        if (shulkerManager.tryOpenBox(item, player, player.getInventory().getHeldItemSlot()))
            player.swingHand(Objects.requireNonNull(e.getHand()));
    }

    private final Logger logger = FeatherExperience.getInstance().getSLF4JLogger();

    @EventHandler
    public void onInvClose(InventoryCloseEvent e)
    {
        var inv = e.getInventory();
        if (inv.getType() != InventoryType.SHULKER_BOX) return;

        if (!(e.getPlayer() instanceof Player player))
            return;

        shulkerManager.closeBox(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        shulkerManager.closeBox(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e)
    {
        //玩家死亡后掉落
        if (e.getPlayer().getHealth() == 0d)
            return;

        var entrySet = shulkerManager.getPlayerEntryMeta(e.getPlayer());

        if (entrySet == null) return;

        var drop = e.getItemDrop().getItemStack();

        if (drop.equals(entrySet.getValue().stack()))
            e.setCancelled(true);
    }

    public void onDisable()
    {
        //不关闭打开的背包可能会导致物品复制
        Bukkit.getOnlinePlayers().forEach(shulkerManager::closeBox);
    }
}
