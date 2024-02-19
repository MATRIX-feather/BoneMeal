package xyz.nifeather.fexp.features.bonemeal;

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

public class BonemealHandler extends FPluginObject
{
    private final Bindable<Boolean> enableFlower = new Bindable<>(false);
    private final Bindable<Boolean> enableCoral = new Bindable<>(false);
    private final Bindable<Boolean> enableSugarcane = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableFlower, ConfigOption.FEAT_BONEMEAL_ON_FLOWER);
        config.bind(enableCoral, ConfigOption.FEAT_BONEMEAL_ON_CORAL);
        config.bind(enableSugarcane, ConfigOption.FEAT_BONEMEAL_ON_SUGARCANE);
    }

    /**
     * @return 执行是否成功
     */
    public boolean onBonemeal(ItemStack stack, Block targetBlock)
    {
        if (stack.getType() != Material.BONE_MEAL) return false;

        var blockMaterial = targetBlock.getType();

        boolean executeSuccess = false;

        if (MaterialTypes.isSmallFlower(blockMaterial))
        {
            executeSuccess = useOnFlower(targetBlock);
        }
        else if (blockMaterial == Material.SUGAR_CANE)
        {
            executeSuccess = useOnSugarcane(targetBlock);
        }
        else if (MaterialTypes.isCoral(blockMaterial))
        {
            executeSuccess = useOnCoral(targetBlock);
        }

        return executeSuccess;
    }

    private boolean useOnFlower(Block block)
    {
        if (!enableFlower.get()) return false;

        var world = block.getWorld();

        // Spawn item for flowers
        world.dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));

        return true;
    }

    private boolean useOnSugarcane(Block block)
    {
        if (!enableSugarcane.get()) return false;

        var world = block.getWorld();

        // Check for total height
        var searchLoc = block.getLocation().add(0.5, 0, 0.5);
        var caneHeight = 0;

        // Check for lower blocks
        for (int i = -1; i > -3; i--)
        {
            var pos = searchLoc.clone().add(0, i, 0);

            var currentBlock = world.getBlockAt(pos);
            if (currentBlock.getType() == Material.SUGAR_CANE)
                caneHeight++;
            else
                break;
        }

        // Check for upper blocks
        Block upperBlock = null;

        for (int i = 0; i < 3; i++)
        {
            var pos = searchLoc.clone().add(0, i, 0);

            var currentBlock = world.getBlockAt(pos);
            if (currentBlock.getType() == Material.SUGAR_CANE)
                caneHeight++;
            else
            {
                upperBlock = currentBlock;
                break;
            }
        }

        if (caneHeight >= 3) return false;

        // Set block
        upperBlock = upperBlock == null ? world.getBlockAt(searchLoc) : upperBlock;

        if (upperBlock.getType().isAir())
        {
            upperBlock.setType(Material.SUGAR_CANE);
            return true;
        }

        return false;
    }

    private final CustomCoralFeature customCoralFeature = new CustomCoralFeature(NoneFeatureConfiguration.CODEC);

    private boolean useOnCoral(Block block)
    {
        if (!enableCoral.get()) return false;

        var world = block.getWorld();
        var serverLevel = ((CraftBlock)block).getHandle().getMinecraftWorld();

        var bukkitPos = block.getLocation();
        var blockLoc = new BlockPos(bukkitPos.getBlockX(), bukkitPos.getBlockY(), bukkitPos.getBlockZ());

        // Check blocks
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
            logger.warn("Unable to place feature since we can't place feature at world '%s'"
                    .formatted(serverLevel.dimension().location().toString()));

            return false;
        }

        // 使用自定义的CoralFeature来允许我们指定要放置的类型
        var material = block.getType();
        customCoralFeature.setCoralType(this.coralToBlockMaterial(material));

        ChunkGenerator generator = levelStem.generator();
        var configuration = NoneFeatureConfiguration.INSTANCE;

        // Place feature
        return customCoralFeature.place(configuration, serverLevel,
                generator, RandomSource.create(), blockLoc);
    }

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
}
