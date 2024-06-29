package xyz.nifeather.fexp.features.mobbucket;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.utilities.NmsRecord;

import java.util.List;
import java.util.UUID;

public class MobBucketListener extends FPluginObject implements Listener
{
    private final MobBucketHandler bucketHandler = new MobBucketHandler();

    private void onEntityInteract(PlayerInteractEntityEvent e)
    {
        var item = e.getPlayer().getEquipment().getItem(e.getHand());

        if (!e.getPlayer().hasPermission(CommonPermissions.mobEggUse))
            return;

        if (bucketHandler.onInteract(item, e.getRightClicked(), e.getPlayer()))
        {
            var nmsPlayer = NmsRecord.ofPlayer(e.getPlayer());

            if (nmsPlayer.gameMode.isSurvival())
                item.setAmount(item.getAmount() - 1);

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillager(PlayerInteractAtEntityEvent e)
    {
        this.onEntityInteract(e);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e)
    {
        this.onEntityInteract(e);
    }
}
