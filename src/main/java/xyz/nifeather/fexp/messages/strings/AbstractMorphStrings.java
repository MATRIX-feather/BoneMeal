package xyz.nifeather.fexp.messages.strings;

import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.pluginbase.Messages.IStrings;
import xyz.nifeather.fexp.FeatherExperience;

public abstract class AbstractMorphStrings implements IStrings
{
    private static final String nameSpace = FeatherExperience.namespace();

    protected static FormattableMessage getFormattable(String key, String defaultValue)
    {
        return new FormattableMessage(nameSpace, key, defaultValue);
    }
}
