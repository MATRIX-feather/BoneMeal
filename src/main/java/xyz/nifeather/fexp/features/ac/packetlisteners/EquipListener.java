package xyz.nifeather.fexp.features.ac.packetlisteners;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ChargedProjectiles;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EquipListener extends AbstractListener
{
    private final List<Enchantment> fakeEnchantments = List.of(
            Enchantment.builder()
                    .type(EnchantmentTypes.SHARPNESS)
                    .level(Byte.MAX_VALUE)
                    .build()
    );

    private final List<ComponentType<?>> componentTypeWhiteList = ObjectList.of(
            ComponentTypes.TRIM, ComponentTypes.ENCHANTMENTS,
            ComponentTypes.CUSTOM_MODEL_DATA, ComponentTypes.ITEM_NAME,
            ComponentTypes.POTION_CONTENTS, ComponentTypes.BANNER_PATTERNS,
            ComponentTypes.PROFILE, ComponentTypes.BASE_COLOR,
            ComponentTypes.DYED_COLOR, ComponentTypes.POT_DECORATIONS,
            ComponentTypes.CHARGED_PROJECTILES, ComponentTypes.FIREWORK_EXPLOSION,
            ComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, ComponentTypes.RARITY
    );

    private final Bindable<Boolean> enableWhitelist = new Bindable<>(true);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableWhitelist, FConfigOptions.COMPONENT_WHITELIST);
    }

    @Override
    protected void onPacketSending(PacketSendEvent event)
    {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        var wrapper = new WrapperPlayServerEntityEquipment(event);
        var user = event.getUser();
        var userClientVersion = user.getClientVersion();

        wrapper.getEquipment().forEach(equipment ->
        {
            var item = equipment.getItem();

            if (enableWhitelist.get())
            {
                var patches = new Object2ObjectArrayMap<>(item.getComponents().getPatches());
                patches.forEach((type, value) ->
                {
                    if (!componentTypeWhiteList.contains(type))
                        item.unsetComponent(type);

                    // Do we really need these in a normal survival?
                    /*
                    if (type == ComponentTypes.CHARGED_PROJECTILES && value.isPresent())
                    {
                        var projectiles = (ChargedProjectiles) value.get();
                        var componentMap = new PatchableComponentMap(new Object2ObjectArrayMap<>());
                        projectiles.getItems().forEach(stack -> stack.setComponents(componentMap));
                    }
                    */
                });
            }

            if (item.isEnchanted(userClientVersion))
                item.setEnchantments(fakeEnchantments, userClientVersion);

            item.setAmount(Integer.MAX_VALUE);

            item.setDamageValue(Integer.MAX_VALUE);
            item.setLegacyData(0);
        });
    }
}
