package xyz.nifeather.fexp.features.serverLink;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerServerLinks;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerLinks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.BindableList;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerLinkHandler extends FPluginObject
{
    private final BindableList<String> serverLinksRaw = new BindableList<>(List.of());

    private final AtomicBoolean scheduleUpdate = new AtomicBoolean(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(serverLinksRaw, FConfigOptions.SERVER_LINKS, String.class);

        serverLinksRaw.onListChanged((changes, reason) ->
        {
            if (scheduleUpdate.get())
                return;

            this.addSchedule(() ->
            {
                this.refresh();
                this.updateForAll();
            });

            scheduleUpdate.set(true);
        }, true);
    }

    private final List<WrapperCommonServerServerLinks.ServerLink> links = Collections.synchronizedList(new ObjectArrayList<>());

    private final Gson gson = new GsonBuilder().create();

    private void refresh()
    {
        logger.info("Building server links...");

        links.clear();

        for (String linkRaw : serverLinksRaw)
        {
            List<String> split = getStrings(linkRaw);

            if (split.size() != 3)
            {
                logger.warn("Illegal link argument '%s': Not enough parameters".formatted(linkRaw));
                continue;
            }

            String knownTypeString = split.get(0);

            var knownType = Arrays.stream(WrapperCommonServerServerLinks.KnownType.values())
                    .filter(type -> type.name().equalsIgnoreCase(knownTypeString))
                    .findFirst().orElse(null);

            String customNameString = split.get(1);
            Component customName = customNameString.equalsIgnoreCase("nil") || customNameString.isBlank()
                    ? null
                    : MiniMessage.miniMessage().deserializeOrNull(customNameString);

            String link = split.get(2);

            if (knownType != null && customName != null)
            {
                logger.warn("Both DisplayName and KnownType is set for link '%s'! Ignoring KnownType...".formatted(link));
                knownType = null;
            }

            if (knownType == null && customName == null)
            {
                logger.warn("Both DisplayName and KnownType is not set for link '%s'!".formatted(link));
                customName = Component.text(link);
            }

            links.add(createLink(knownType, customName, link));
        }

        logger.info("Done building server links.");
    }

    private @NotNull List<String> getStrings(String linkRaw)
    {
        List<String> split = new ObjectArrayList<>();
        StringBuilder builder = null;
        var charArray = linkRaw.toCharArray();

        for (int i = 0; i < charArray.length; i++)
        {
            if (builder == null) builder = new StringBuilder();

            var charAtCurrent = charArray[i];
            char charAtLast = i == 0 ? '_' : charArray[i-1];

            if (charAtCurrent == '\\') continue;

            if (charAtCurrent == ',' && charAtLast != '\\')
            {
                split.add(builder.toString());
                builder = null;
                continue;
            }

            builder.append(charAtCurrent);
        }

        if (builder != null)
            split.add(builder.toString());

        return split;
    }

    public void updateForAll()
    {
        logger.info("Broadcasting server link to all players...");

        scheduleUpdate.set(false);

        for (World world : Bukkit.getWorlds())
        {
            var players = world.getPlayers();
            players.forEach(this::handlePlayer);
        }

        logger.info("Broadcasting server link to all players... Done.");
    }

    public void handlePlayer(Player player)
    {
        var packetEvents = PacketEvents.getAPI();

        var packet = new WrapperPlayServerServerLinks(this.links);
        packetEvents.getPlayerManager().getUser(player).sendPacket(packet);
    }

    private WrapperCommonServerServerLinks.ServerLink createLink(
            @Nullable WrapperCommonServerServerLinks.KnownType knownType,
            @Nullable Component customName,
            String url)
    {
        return new WrapperCommonServerServerLinks.ServerLink(knownType, customName, url);
    }
}
