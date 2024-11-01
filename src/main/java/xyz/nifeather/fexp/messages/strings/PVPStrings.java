package xyz.nifeather.fexp.messages.strings;

import xiamomc.pluginbase.Messages.FormattableMessage;

public class PVPStrings extends AbstractMorphStrings
{
    public static FormattableMessage pvpDisabledForDamagerString()
    {
        return getFormattable(getKey("damager_pvp_disabled"), "[Fallback] <red>你已禁用PVP");
    }

    public static FormattableMessage pvpDisabledForVictimString()
    {
        return getFormattable(getKey("victim_pvp_disabled"), "[Fallback] <red>对方已禁用PVP");
    }

    public static String getKey(String key)
    {
        return "pvp." + key;
    }
}
