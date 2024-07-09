package xyz.nifeather.fexp.features.ac.eventlisteners;

import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import xyz.nifeather.fexp.FPluginObject;

import java.util.List;
import java.util.Map;

public class BeaconListener extends FPluginObject implements Listener
{
    private final List<PotionEffectType> firstTierValidTypes = List.of(
            PotionEffectType.SPEED, PotionEffectType.HASTE
    );

    private final Map<Integer, List<PotionEffectType>> validTypes = new Object2ObjectArrayMap<>();

    public BeaconListener()
    {
        validTypes.put(0, List.of());
        validTypes.put(1, List.of(PotionEffectType.SPEED, PotionEffectType.HASTE));
        validTypes.put(2, List.of(PotionEffectType.RESISTANCE, PotionEffectType.JUMP_BOOST));
        validTypes.put(3, List.of(PotionEffectType.STRENGTH));
        validTypes.put(4, List.of());
    }

    private List<PotionEffectType> getValidTypesForTier(int tier)
    {
        List<PotionEffectType> valid = new ObjectArrayList<>();
        List<PotionEffectType> empty = List.of();

        for (int i = 0; i <= tier; i++)
            valid.addAll(this.validTypes.getOrDefault(i, empty));

        return valid;
    }

    @EventHandler
    public void onBeacon(PlayerChangeBeaconEffectEvent event)
    {
        var block = event.getBeacon();
        var blockState = block.getState();

        if (!(blockState instanceof Beacon beacon))
        {
            logger.warn("Received a PlayerChangeBeaconEffectEvent but the block is not a beacon?!");
            logger.warn("Block is " + block + " and state is " + blockState);
            return;
        }

        var primaryEffect = event.getPrimary();
        var secondaryEffect = event.getSecondary();

        var valid = this.getValidTypesForTier(beacon.getTier());
        if (!valid.contains(primaryEffect))
            event.setCancelled(true);

        if (beacon.getTier() != 4 && secondaryEffect != null)
            event.setCancelled(true);
    }
}
