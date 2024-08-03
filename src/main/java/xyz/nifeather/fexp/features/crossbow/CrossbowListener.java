package xyz.nifeather.fexp.features.crossbow;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import xyz.nifeather.fexp.FPluginObject;

import java.util.List;

public class CrossbowListener extends FPluginObject implements Listener
{
    @EventHandler
    public void onCrossbowLoad(EntityLoadCrossbowEvent event)
    {
        var entity = event.getEntity();
        var crossbow = event.getCrossbow();

        if (entity.getType() != EntityType.PLAYER) return;

        var itemInOffhand = entity.getEquipment().getItemInOffHand();
        if (itemInOffhand.getType() != Material.TORCH && itemInOffhand.getType() != Material.SOUL_TORCH) return;

        event.setCancelled(true);

        crossbow.editMeta(CrossbowMeta.class, meta ->
                meta.setChargedProjectiles(List.of(new ItemStack(itemInOffhand.getType(), 1))));

        if (((CraftPlayer) entity).getHandle().gameMode.isSurvival())
            itemInOffhand.setAmount(itemInOffhand.getAmount() - 1);
    }

    @EventHandler
    public void onProjectileLaunch(EntityShootBowEvent event)
    {
        var bow = event.getBow();
        var itemConsumed = event.getConsumable();

        if (bow == null || bow.getType() != Material.CROSSBOW) return;

        if (itemConsumed == null || (itemConsumed.getType() != Material.TORCH && itemConsumed.getType() != Material.SOUL_TORCH)) return;

        var fallingBlock = event.getEntity().getWorld().spawnFallingBlock(event.getProjectile().getLocation(), itemConsumed.getType(), (byte)0);
        fallingBlock.setHurtEntities(false);
        fallingBlock.setVelocity(event.getProjectile().getVelocity());
        fallingBlock.shouldAutoExpire(true);

        event.setProjectile(fallingBlock);
    }
}
