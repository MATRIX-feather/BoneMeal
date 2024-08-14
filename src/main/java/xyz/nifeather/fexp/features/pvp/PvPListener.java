package xyz.nifeather.fexp.features.pvp;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.features.pvp.storage.PVPStorage;
import xyz.nifeather.fexp.messages.strings.PVPStrings;
import xyz.nifeather.fexp.utilities.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PvPListener extends FPluginObject implements Listener
{
    private final List<UUID> noPvpPlayers = Collections.synchronizedList(new ObjectArrayList<>());

    private final PVPStorage storage = new PVPStorage();

    public PvPListener()
    {
        storage.initializeStorage();
        noPvpPlayers.addAll(storage.getDisabledPlayers());
    }

    private final Bindable<Boolean> enabled = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enabled, FConfigOptions.PVP_TOGGLE_ENABLED);
    }

    public PvPStatus toggleFor(Player player)
    {
        return this.toggleFor(player.getUniqueId());
    }

    public PvPStatus toggleFor(UUID player)
    {
        if (noPvpPlayers.stream().anyMatch(uuid -> uuid.equals(player)))
        {
            noPvpPlayers.removeIf(uuid -> uuid.equals(player));
            return PvPStatus.ENABLED;
        }
        else
        {
            noPvpPlayers.add(player);
            return PvPStatus.DISABLED;
        }
    }

    public boolean isPlayerDisabledPVP(Player player)
    {
        return isPlayerDisabledPVP(player.getUniqueId());
    }

    public boolean isPlayerDisabledPVP(UUID player)
    {
        return noPvpPlayers.stream().anyMatch(uuid -> uuid.equals(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityHurtEntity(EntityDamageByEntityEvent event)
    {
        if (!enabled.get()) return;

        var entity = event.getEntity();
        var damager = event.getDamager();

        if (entity.getType() != EntityType.PLAYER || damager.getType() != EntityType.PLAYER)
            return;

        if (isPlayerDisabledPVP(damager.getUniqueId()))
        {
            damager.sendActionBar(MessageUtils.prefixes(damager, PVPStrings.pvpDisabledForDamagerString()));
            event.setCancelled(true);
        }

        if (isPlayerDisabledPVP(entity.getUniqueId()))
        {
            damager.sendActionBar(MessageUtils.prefixes(damager, PVPStrings.pvpDisabledForVictimString()));
            event.setCancelled(true);
        }
    }

    public void dispose()
    {
        storage.setPlayers(this.noPvpPlayers);
        storage.saveConfiguration();
    }
}
