package xyz.nifeather.fexp.features.bonemeal.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.MaterialTypes;
import xyz.nifeather.fexp.config.ConfigOption;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.bonemeal.CustomCoralFeature;
import xyz.nifeather.fexp.features.bonemeal.IBonemealHandler;

public class CoralHandler extends FPluginObject implements IBonemealHandler
{
    /**
     * @return The identifier of this handler
     */
    @Override
    public String getIdentifier()
    {
        return "coral";
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
        return useOnCoral(block);
    }

    private final CustomCoralFeature customCoralFeature = new CustomCoralFeature(NoneFeatureConfiguration.CODEC);

    private boolean useOnCoral(Block block)
    {
        if (!enableCoral.get()) return false;

        var world = block.getWorld();
        var serverLevel = ((CraftBlock)block).getHandle().getMinecraftWorld();

        var bukkitPos = block.getLocation();
        var blockLoc = new BlockPos(bukkitPos.getBlockX(), bukkitPos.getBlockY(), bukkitPos.getBlockZ());

        // Check if the block below is coral-plantable
        var blockBelow = world.getBlockAt(bukkitPos.clone().add(0, -1, 0));
        if (!MaterialTypes.isCoralPlantable(blockBelow.getType())) return false;

        // Get LevelStem
        var logger = FeatherExperience.getInstance().getSLF4JLogger();
        Registry<LevelStem> dimensionRegistry = null;

        try
        {
            dimensionRegistry = serverLevel.getServer().registryAccess()
                    .registryOrThrow(Registries.LEVEL_STEM);
        }
        catch (Throwable t)
        {
            logger.error("Unable to place feature: %s".formatted(t.getLocalizedMessage()));
            t.printStackTrace();
        }

        if (dimensionRegistry == null) return false;

        var key = serverLevel.dimension().location();
        LevelStem levelStem = dimensionRegistry.get(key);

        if (levelStem == null)
        {
            logger.warn("We can't place feature at world '%s'"
                    .formatted(serverLevel.dimension().location().toString()));

            return false;
        }

        // Use a custom CoralFeature to allow us to place specific types of coral
        var material = block.getType();
        customCoralFeature.setCoralType(this.coralToBlockMaterial(material));

        ChunkGenerator generator = levelStem.generator();
        var configuration = NoneFeatureConfiguration.INSTANCE;

        // Place feature
        return customCoralFeature.place(configuration, serverLevel,
                generator, randomSource, blockLoc);
    }

    private final RandomSource randomSource = RandomSource.create();

    @Nullable
    private Material coralToBlockMaterial(Material input)
    {
        return switch (input)
        {
            case TUBE_CORAL -> Material.TUBE_CORAL_BLOCK;
            case BRAIN_CORAL -> Material.BRAIN_CORAL_BLOCK;
            case BUBBLE_CORAL -> Material.BUBBLE_CORAL_BLOCK;
            case FIRE_CORAL -> Material.FIRE_CORAL_BLOCK;
            case HORN_CORAL -> Material.HORN_CORAL_BLOCK;
            default -> null;
        };
    }

    private final Bindable<Boolean> enableCoral = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableCoral, ConfigOption.FEAT_BONEMEAL_ON_CORAL);
    }
}
