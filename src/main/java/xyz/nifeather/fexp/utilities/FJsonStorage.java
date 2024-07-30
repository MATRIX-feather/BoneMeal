package xyz.nifeather.fexp.utilities;

import xiamomc.pluginbase.JsonBasedStorage;
import xyz.nifeather.fexp.FeatherExperience;

public abstract class FJsonStorage<T> extends JsonBasedStorage<T, FeatherExperience>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }
}
