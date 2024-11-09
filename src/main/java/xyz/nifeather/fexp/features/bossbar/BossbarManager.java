package xyz.nifeather.fexp.features.bossbar;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.fexp.FPluginObject;

import java.util.Map;
import java.util.UUID;

public class BossbarManager extends FPluginObject
{
    private final Map<UUID, BossbarHolder> registry = new Object2ObjectOpenHashMap<>();

    public BossbarHolder register(LivingEntity entity)
    {
        var uuid = entity.getUniqueId();

        if (registry.containsKey(uuid))
            return registry.get(uuid);

        var holder = new BossbarHolder(entity);

        registry.put(uuid, holder);

        return holder;
    }

    @Nullable
    public BossbarHolder get(LivingEntity entity)
    {
        return registry.getOrDefault(entity.getUniqueId(), null);
    }

    public void drop(LivingEntity entity)
    {
        // entity#getUniqueId() can throw off-main thread error on event trigger, why?
        var uuid = ((CraftEntity) entity).getHandleRaw().getUUID();

        var holder = registry.remove(uuid);
        if (holder != null)
            holder.dispose();
    }
}
