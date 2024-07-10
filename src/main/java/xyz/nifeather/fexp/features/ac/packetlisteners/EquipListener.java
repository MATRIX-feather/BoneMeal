package xyz.nifeather.fexp.features.ac.packetlisteners;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

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

            /*
            var patches = new Object2ObjectArrayMap<>(item.getComponents().getPatches());
            patches.forEach((type, value) ->
            {
                if (type != ComponentTypes.TRIM
                        && type != ComponentTypes.ENCHANTMENTS
                        && type != ComponentTypes.CUSTOM_MODEL_DATA
                        && type != ComponentTypes.ITEM_NAME
                        && type != ComponentTypes.POTION_CONTENTS
                        && type != ComponentTypes.BANNER_PATTERNS)
                {
                    item.unsetComponent(type);
                }
            });
            */

            if (item.isEnchanted(userClientVersion))
                item.setEnchantments(fakeEnchantments, userClientVersion);

            item.setAmount(Integer.MAX_VALUE);

            item.setDamageValue(Integer.MAX_VALUE);
            item.setLegacyData(0);
        });
    }
}
