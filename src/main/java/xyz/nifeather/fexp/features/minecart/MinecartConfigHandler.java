package xyz.nifeather.fexp.features.minecart;

import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.pluginbase.Bindables.TriggerReason;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.List;
import java.util.Map;

public class MinecartConfigHandler extends FPluginObject
{
    private final BindableList<String> rawConfig = new BindableList<>(List.of());
    private final Bindable<Boolean> enabled = new Bindable<>(false);

    private final AtomicDouble minecartDefaultSpeed = new AtomicDouble(0.4d);

    @Initializer
    private void load(FConfigManager fConfigManager)
    {
        fConfigManager.bind(rawConfig, FConfigOptions.MINECART_CONFIGS, String.class);
        fConfigManager.bind(enabled, FConfigOptions.MINECART);

        rawConfig.onListChanged(this::updateList, true);

        initDefaultMaxSpeed();
    }

    private void initDefaultMaxSpeed()
    {
        var world = Bukkit.getWorlds().getFirst();
        var serverWorld = ((CraftWorld) world).getHandle();
        var entity = EntityType.MINECART.create(serverWorld, this::scheduleEntityDiscard, BlockPos.ZERO, MobSpawnType.COMMAND, false, false);

        if (entity != null)
            this.minecartDefaultSpeed.set(entity.maxSpeed);
        else
            logger.warn("Can't get default max speed for minecart, using 0.4...");
    }

    private void scheduleEntityDiscard(Entity nmsEntity)
    {
        var entity = nmsEntity.getBukkitEntity();
        entity.getScheduler()
                .run(FeatherExperience.getInstance(), retiredTask -> {}, entity::remove);
    }

    // Type <-> Multiplier
    private final Map<Material, Double> configurations = new Object2ObjectArrayMap<>();

    public double getSpeedMultiplier(Material material)
    {
        return configurations.getOrDefault(material, 1d);
    }

    public boolean enabled()
    {
        return enabled.get();
    }

    public double getDefaultMaxSpeed()
    {
        return minecartDefaultSpeed.get();
    }

    private void updateList(List<String> diff, TriggerReason reason)
    {
        for (String line : diff)
        {
            var strip = line.split("=");
            if (strip.length != 2)
            {
                logger.error("Line '%s' has a wrong format, it should be like 'minecraft:bedrock=1.5'".formatted(line));
                return;
            }

            var type = Material.matchMaterial(strip[0]);
            logger.info("ID %s Matches %s, reason is %s".formatted(strip[0], type, reason));

            if (type == null) continue;
            if (reason == TriggerReason.REMOVE)
            {
                configurations.remove(type);
                return;
            }

            // ADD

            double multiplier = 1d;
            try
            {
                multiplier = Double.parseDouble(strip[1]);
            }
            catch (Throwable t)
            {
                logger.error("Can't parse '%s' into a number: '%s'".formatted(strip[1], t.getMessage()));
            }

            configurations.put(type, multiplier);
        }
    }
}
