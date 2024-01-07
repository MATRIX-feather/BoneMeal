package xiamomc.bonemeal.features.bonemeal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xiamomc.bonemeal.MaterialTypes;
import xiamomc.bonemeal.XiaMoExperience;

import java.util.List;

public class BonemealListener implements Listener
{
    private final BonemealHandler handler = new BonemealHandler();

    private final Logger logger = XiaMoExperience.getInstance().getSLF4JLogger();

    @EventHandler
    public void onDispense(BlockDispenseEvent e)
    {
        var sourceBlock = e.getBlock();

        if (sourceBlock.getType() != Material.DISPENSER)
            return;

        var dispenser = (Dispenser)sourceBlock.getState();

        if (!(dispenser.getBlockData() instanceof Directional directional))
        {
            logger.warn("A dispenser has a BlockData that's not a Directional?!");
            return;
        }

        var facing = directional.getFacing();
        var targetLoc = dispenser.getLocation();
        targetLoc.setX(targetLoc.x() + facing.getModX());
        targetLoc.setY(targetLoc.y() + facing.getModY());
        targetLoc.setZ(targetLoc.z() + facing.getModZ());

        var item = e.getItem();
        var targetBlock = sourceBlock.getWorld().getBlockAt(targetLoc);

        if (!handler.onBonemeal(item, targetBlock))
            return;

        var world = targetBlock.getWorld();
        var location = targetBlock.getLocation().add(0.5, 0.5, 0.5);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 20, 0.25, 0.25, 0.25);

        e.setItem(new ItemStack(Material.AIR, 64));

        for (ItemStack i : dispenser.getInventory())
        {
            if (i == null) return;

            if (i.getType() != Material.BONE_MEAL) continue;

            i.setAmount(i.getAmount() - 1);

            if (i.getAmount() <= 0)
                i.setType(Material.AIR);

            break;
        }
    }

    private final List<Player> rightClicked = new ObjectArrayList<>();

    @EventHandler
    public void onUseItem(PlayerInteractEvent e)
    {
        // If not clicking on a block, skip
        if (!e.hasBlock()) return;

        // Check action
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = e.getPlayer();

        if (rightClicked.remove(player) && e.getHand() == EquipmentSlot.OFF_HAND)
            return;

        // Get block and its material etc.
        var block = e.getClickedBlock();
        assert block != null;

        var item = e.getItem();

        if (item == null) return;
        if (item.getType() != Material.BONE_MEAL) return;

        if (!handler.onBonemeal(item, block))
            return;
        else
            item.setAmount(item.getAmount() - 1);

        // Add to block
        if (e.getHand() == EquipmentSlot.HAND)
            rightClicked.add(player);

        // Determine which hand to swing
        var hand = e.getHand();

        if (hand == EquipmentSlot.HAND)
            player.swingMainHand();
        else
            player.swingOffHand();

        // Spawn particle and play sound
        var world = block.getWorld();
        var location = block.getLocation().add(0.5, 0.5, 0.5);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 20, 0.25, 0.25, 0.25);
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 1, 1);
    }
}
