package xyz.nifeather.fexp.features.bossbar;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.fexp.FPluginObject;

import java.util.Map;

public class BossbarManager extends FPluginObject
{
    private final Map<LivingEntity, BossbarHolder> registry = new Object2ObjectArrayMap<>();

    public BossbarHolder register(LivingEntity entity)
    {
        if (registry.containsKey(entity))
            return registry.get(entity);

        var holder = new BossbarHolder(entity);

        registry.put(entity, holder);

        return holder;
    }

    @Nullable
    public BossbarHolder get(LivingEntity entity)
    {
        return registry.getOrDefault(entity, null);
    }

    public void drop(LivingEntity entity)
    {
        var holder = registry.remove(entity);
        if (holder != null)
            holder.dispose();
    }
}
