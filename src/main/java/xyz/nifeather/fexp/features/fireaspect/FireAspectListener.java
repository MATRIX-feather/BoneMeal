package xyz.nifeather.fexp.features.fireaspect;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.Candle;
import org.bukkit.craftbukkit.block.impl.CraftCandleCake;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.List;

public class FireAspectListener extends FPluginObject implements Listener
{
    private final Bindable<Boolean> enabled = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enabled, FConfigOptions.ALLOW_FIRE_ASPECT_LIT_CANDLES);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock() || !enabled.get() || !e.getAction().isLeftClick()) return;

        var item = e.getItem();
        if (item == null || !item.getEnchantments().containsKey(Enchantment.FIRE_ASPECT)) return;

        var block = e.getClickedBlock();
        if (block == null) return; // Make IDEA happy

        var blockData = block.getBlockData();
        boolean changed = true;
        var player = e.getPlayer();

        switch (blockData)
        {
            case Candle candle ->
            {
                if (!candle.isWaterlogged())
                    candle.setLit(true);
            }

            case CraftCandleCake candleCake ->
            {
                candleCake.setLit(true);
            }

            default -> { changed = false; }
        }

        block.setBlockData(blockData);
    }
}
