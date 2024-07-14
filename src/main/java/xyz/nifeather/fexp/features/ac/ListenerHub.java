package xyz.nifeather.fexp.features.ac;

import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import xiamomc.pluginbase.Annotations.Initializer;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.features.ac.eventlisteners.BeaconListener;
import xyz.nifeather.fexp.features.ac.packetlisteners.EquipListener;
import xyz.nifeather.fexp.features.ac.packetlisteners.MetadataListener;

import java.util.List;

public class ListenerHub extends FPluginObject
{
    @Initializer
    private void load()
    {
        var listeners = List.of(
                new EquipListener(),
                new MetadataListener()
        );

        listeners.forEach(l -> PacketEvents.getAPI().getEventManager().registerListener(l.listenerWrapper()));
    }
}
