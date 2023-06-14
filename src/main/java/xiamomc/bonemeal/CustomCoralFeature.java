package xiamomc.bonemeal;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.Material;

import java.util.Optional;

public class CustomCoralFeature extends CoralTreeFeature
{
    public CustomCoralFeature(Codec<NoneFeatureConfiguration> configCodec)
    {
        super(configCodec);
    }

    public void setCoralType(Material material)
    {
        this.coralMaterial = material;
    }

    private Material coralMaterial;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        var optional = BuiltInRegistries.BLOCK.getTag(BlockTags.CORAL_BLOCKS)
                .flatMap(b ->
                {
                    for (Holder<Block> holder : b)
                    {
                        var block = holder.value();
                        var bukkitMaterial = block.defaultBlockState().getBukkitMaterial();

                        if (bukkitMaterial.equals(this.coralMaterial))
                            return Optional.of(block);
                    }

                    return Optional.empty();
                });

        return optional.isEmpty()
                ? false
                : this.placeFeature(context.level(), context.random(),
                                    context.origin(), optional.get().defaultBlockState());
    }
}
