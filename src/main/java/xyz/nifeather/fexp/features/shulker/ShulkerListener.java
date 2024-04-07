package xyz.nifeather.fexp.features.shulker;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.config.FConfigManager;

import java.util.Map;
import java.util.Objects;

public class ShulkerListener extends FPluginObject implements Listener
{
    private final ShulkerManager shulkerManager = new ShulkerManager();

    private final Bindable<Boolean> enabled = new Bindable<>(false);
    private final Bindable<Integer> disbaledTime = new Bindable<>();

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enabled, FConfigOptions.FEAT_OPEN_SHULKERBOX);
        config.bind(disbaledTime, FConfigOptions.SHULKERBOX_OPEN_DELAY);
    }

    private final Map<Player, Long> playerJoinTimeMap = new Object2ObjectArrayMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        playerJoinTimeMap.put(e.getPlayer(), plugin.getCurrentTick());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if (!enabled.get()) return;

        var item = e.getItem();
        if (item == null)
            return;

        if (!e.getAction().isRightClick() || e.getClickedBlock() != null) return;

        var player = e.getPlayer();

        if (plugin.getCurrentTick() - playerJoinTimeMap.getOrDefault(player, 0L) <= disbaledTime.get())
            return;

        playerJoinTimeMap.remove(player);

        //如果打开了别的盒子，那么不要处理
        if (shulkerManager.openingBox(player))
            return;

        if (shulkerManager.tryOpenBox(item, player, player.getInventory().getHeldItemSlot()))
            player.swingHand(Objects.requireNonNull(e.getHand()));
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e)
    {
        var clicked = e.getClickedInventory();
        var inv = e.getInventory();
        if (clicked == null || inv.getType() != InventoryType.SHULKER_BOX) return;

        var openMeta = shulkerManager.getOpenMeta(inv);
        if (openMeta == null)
            return;

        if (openMeta.stack().isSimilar(e.getCurrentItem()))
            e.setCancelled(true);
    }

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

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent e)
    {
        shulkerManager.closeBox(e.getPlayer());
        playerJoinTimeMap.remove(e.getPlayer());
    }

    public void onDisable()
    {
        //不关闭打开的背包可能会导致物品复制
        Bukkit.getOnlinePlayers().forEach(shulkerManager::closeBox);
    }
}
