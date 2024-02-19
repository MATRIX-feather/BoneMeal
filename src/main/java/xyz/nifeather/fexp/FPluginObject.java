package xyz.nifeather.fexp;

import xiamomc.pluginbase.PluginObject;

public class FPluginObject extends PluginObject<FeatherExperience>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }
}
