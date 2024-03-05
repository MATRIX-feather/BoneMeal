package xyz.nifeather.fexp.features.mobbucket;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.utilities.NmsRecord;

import java.util.List;
import java.util.UUID;

public class MobBucketListener extends FPluginObject implements Listener
{
    private final MobBucketHandler bucketHandler = new MobBucketHandler();

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e)
    {
        var owner = e.getEntity().getOwnerUniqueId();
        if (owner == null || !blockedUUIDs.contains(owner)) return;

        blockedUUIDs.remove(owner);
        e.setCancelled(true);
    }

    private final List<UUID> blockedUUIDs = new ObjectArrayList<>();

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e)
    {
        var item = e.getPlayer().getEquipment().getItem(e.getHand());

        if (bucketHandler.onInteract(item, e.getRightClicked()))
        {
            var nmsPlayer = NmsRecord.ofPlayer(e.getPlayer());

            if (!nmsPlayer.gameMode.isCreative())
                item.setAmount(item.getAmount() - 1);

            blockedUUIDs.add(e.getPlayer().getUniqueId());

            e.setCancelled(true);
        }
    }
}
