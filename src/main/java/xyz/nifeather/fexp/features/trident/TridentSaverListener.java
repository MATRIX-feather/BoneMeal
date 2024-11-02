package xyz.nifeather.fexp.features.trident;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.UUID;

public class TridentSaverListener extends FPluginObject implements Listener
{
    private final Bindable<Boolean> tridentEnabled = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(tridentEnabled, FConfigOptions.TRIDENT);
    }

    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent e)
    {
        if (!(e.getEntity() instanceof Trident trident)) return;
        if (trident.getOwnerUniqueId() == null
            || trident.getLoyaltyLevel() <= 0)
        {
            return;
        }

        this.scheduleOn(trident, () -> this.updateOnTrident(trident));
    }

    private void updateOnTrident(Trident trident)
    {
        if (this.tridentUpdate(trident))
            this.scheduleOn(trident, () -> this.updateOnTrident(trident));
    }

    /**
     *
     * @return Whether this trident is still available to schedule task on
     */
    private boolean tridentUpdate(Trident trident)
    {
        // 如果三叉戟在地上，或者已被移除，则不再更新
        if (trident.isOnGround() || trident.isDead()) return false;

        // 确保半路开启功能时任何处于更新状态的三叉戟都能及时收回
        if (!tridentEnabled.get()) return true;

        // 如果三叉戟在世界高度里，则计划下一轮更新
        if (trident.getLocation().getY() > trident.getWorld().getMinHeight())
           return true;

        // 尝试获取拥有者
        var ownerUUID = trident.getOwnerUniqueId();
        if (ownerUUID == null) return false;

        // 尝试获取其位置
        var ownerLocation = getOwnerLocation(ownerUUID);

        // 如果没有位置，则忽略此三叉戟
        if (ownerLocation == null)
        {
            logger.warn("ownerLocation is null, not recovering trident...");
            return false;
        }

        // 收回此三叉戟
        trident.setHasDealtDamage(true);
        //trident.teleportAsync(ownerLocation);
        return false;
    }

    @Nullable
    private Location getOwnerLocation(UUID uuid)
    {
        var onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null)
            return onlinePlayer.getLocation();

        try
        {
            var offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            return offlinePlayer.getLocation();
        }
        catch (Throwable t)
        {
            logger.warn("Can't get offline player's location. Is the server outdated?");
            logger.warn(t.getMessage());
        }

        return null;
    }
}
