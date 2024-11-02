package xyz.nifeather.fexp.features.ac.packetlisteners;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.minecraft.Util;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.Optional;
import java.util.UUID;

public class MetadataListener extends AbstractListener
{
    private final Bindable<Boolean> enableWhitelist = new Bindable<>(true);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableWhitelist, FConfigOptions.COMPONENT_WHITELIST);
    }

    @Override
    protected void onPacketSending(PacketSendEvent event)
    {
        if (!enableWhitelist.get()) return;

        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;

        var wrapper = new WrapperPlayServerEntityMetadata(event);

/*
        var sourceEntity= SpigotReflectionUtil.getEntityById(wrapper.getEntityId());

        if (sourceEntity == null)
        {
            logger.warn("Null entity for id " + wrapper.getEntityId() + "?!");
            return;
        }

        if (sourceEntity.getType() != EntityType.WOLF) return;
*/

        var playerUUID = ((Player) event.getPlayer()).getUniqueId();

        for (EntityData data : wrapper.getEntityMetadata())
        {
            if (data.getType().equals(EntityDataTypes.OPTIONAL_UUID))
            {
                @Nullable Optional<UUID> optionalUUID = (Optional<UUID>) data.getValue();
                if (optionalUUID == null || optionalUUID.isEmpty()) continue;

                // We don't hide if the UUID equals the player's UUID
                // Because some of the game feature requires it
                // Like the wolf, the client need to know if it's its owner
                if (optionalUUID.get().equals(playerUUID)) continue;

                data.setValue(Optional.of(Util.NIL_UUID));
            }
        }
    }
}
