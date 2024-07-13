package xyz.nifeather.fexp.features.mobbucket;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
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

    public static boolean townyInstalled = false;

    private void onEntityInteract(PlayerInteractEntityEvent e)
    {
        var item = e.getPlayer().getEquipment().getItem(e.getHand());

        var player = e.getPlayer();
        if (!player.hasPermission(CommonPermissions.mobEggUse))
            return;

        if (townyInstalled)
        {
            if (!PlayerCacheUtil.getCachePermission(player, e.getRightClicked().getLocation(), item.getType(), TownyPermission.ActionType.ITEM_USE))
                return;
        }

        if (bucketHandler.onInteract(item, e.getRightClicked(), e.getPlayer()))
        {
            var nmsPlayer = NmsRecord.ofPlayer(e.getPlayer());

            if (nmsPlayer.gameMode.isSurvival())
                item.setAmount(item.getAmount() - 1);

            e.setCancelled(true);

            var playerUUID = e.getPlayer().getUniqueId();
            if (blockedUUIDs.stream().noneMatch(uuid -> uuid.equals(playerUUID)))
                blockedUUIDs.add(playerUUID);
        }
    }

    private final List<UUID> blockedUUIDs = new ObjectArrayList<>();

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e)
    {
        if (e.getEntity().getType() != EntityType.EGG) return;

        var owner = e.getEntity().getOwnerUniqueId();
        if (owner == null) return;

        if (blockedUUIDs.removeIf(uuid -> uuid.equals(owner)))
            e.setCancelled(true);
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
