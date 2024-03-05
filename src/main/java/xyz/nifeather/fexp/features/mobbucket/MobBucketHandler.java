package xyz.nifeather.fexp.features.mobbucket;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntitySnapshot;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Bindables.BindableList;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.List;

public class MobBucketHandler extends FPluginObject
{
    private final Bindable<Boolean> enabled = new Bindable<>(false);
    private BindableList<String> disabledWorlds = new BindableList<>(List.of());

    @Initializer
    private void load(FConfigManager configManager)
    {
        configManager.bind(enabled, FConfigOptions.VILLAGER_EGG);

        disabledWorlds = configManager.getBindableList(String.class, FConfigOptions.EGG_DISABLED_WORLDS);
    }

    /**
     * Whether this operation operates successfully
     * @param item
     * @param clickedEntity
     * @return
     */
    public boolean onInteract(ItemStack item, @Nullable Entity clickedEntity)
    {
        if (!enabled.get()) return false;

        if (item.getType() == Material.EGG)
            return onEmptyInteract(item, clickedEntity);

        return false;
    }

    /**
     * Whether this operation operates successfully
     * @param item
     * @param clickedEntity
     * @return
     */
    private boolean onEmptyInteract(ItemStack item, @Nullable Entity clickedEntity)
    {
        if (clickedEntity == null) return false;

        if (disabledWorlds.stream().anyMatch(s -> s.equalsIgnoreCase(clickedEntity.getWorld().getName())))
            return false;

        // 不允许收集玩家和非LivingEntity的实体
        if (!(clickedEntity instanceof LivingEntity) || clickedEntity instanceof Player)
            return false;

        // 不允许收集Boss
        if (clickedEntity instanceof Boss)
            return false;

        var newItem = ItemStack.empty();
        newItem.setAmount(1);

        var typeName = "%s_SPAWN_EGG".formatted(clickedEntity.getType().name().toUpperCase());
        Material eggType = Material.ALLAY_SPAWN_EGG;

        try
        {
            eggType = Material.valueOf(typeName);
        }
        catch (Throwable t)
        {
            logger.warn("Can't find egg type for type " + clickedEntity.getType());
            logger.warn("Using allay spawn egg as default...");
        }

        newItem.setType(eggType);
        var meta = (SpawnEggMeta)newItem.getItemMeta();

        meta.setCustomSpawnedType(clickedEntity.getType());
        meta.setSpawnedEntity(CraftEntitySnapshot.create((CraftEntity) clickedEntity));

        if (clickedEntity.customName() != null)
            meta.displayName(clickedEntity.customName());

        newItem.setItemMeta(meta);

        this.scheduleOn(clickedEntity, () ->
        {
            var world = clickedEntity.getWorld();
            world.dropItem(clickedEntity.getLocation(), newItem);
            clickedEntity.remove();

            world.playSound(clickedEntity.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        });

        return true;
    }
}
