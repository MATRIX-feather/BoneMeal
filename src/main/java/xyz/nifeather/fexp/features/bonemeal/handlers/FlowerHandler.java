package xyz.nifeather.fexp.features.bonemeal.handlers;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.MaterialTypes;
import xyz.nifeather.fexp.config.ConfigOption;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.bonemeal.IBonemealHandler;

public class FlowerHandler extends FPluginObject implements IBonemealHandler
{
    /**
     * @return The identifier of this handler
     */
    @Override
    public String getIdentifier()
    {
        return "flower";
    }

    /**
     * Called when bone meal is used on the given block
     *
     * @param block The block that been interacted with bone meal
     * @return True if we should consume the bone meal, otherwise False
     */
    @Override
    public boolean onBonemeal(Block block)
    {
        if (!enableFlower.get()) return false;

        if (!MaterialTypes.isSmallFlower(block.getType())) return false;

        var world = block.getWorld();

        // Spawn item for flowers
        world.dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));

        return true;
    }

    private final Bindable<Boolean> enableFlower = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableFlower, ConfigOption.FEAT_BONEMEAL_ON_FLOWER);
    }
}
