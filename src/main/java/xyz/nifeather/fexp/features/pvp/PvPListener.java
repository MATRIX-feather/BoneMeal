package xyz.nifeather.fexp.features.pvp;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.nifeather.fexp.FPluginObject;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PvPListener extends FPluginObject implements Listener
{
    private final List<UUID> noPvpPlayers = Collections.synchronizedList(new ObjectArrayList<>());

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

    public boolean isPvPDisabledFor(Player player)
    {
        return isPvPDisabledFor(player.getUniqueId());
    }

    public boolean isPvPDisabledFor(UUID player)
    {
        return noPvpPlayers.stream().anyMatch(uuid -> uuid.equals(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityHurtEntity(EntityDamageByEntityEvent event)
    {
        if (this.isPvPDisabledFor(event.getEntity().getUniqueId()))
            event.setCancelled(true);
    }
}
