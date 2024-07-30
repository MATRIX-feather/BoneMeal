package xyz.nifeather.fexp.features.pvp.storage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

public class DataRoot
{
    @SerializedName("data")
    @Expose
    public List<UUID> disabledPlayers = new ObjectArrayList<>();
}
