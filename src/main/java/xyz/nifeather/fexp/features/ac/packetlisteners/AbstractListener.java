package xyz.nifeather.fexp.features.ac.packetlisteners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.nifeather.fexp.FPluginObject;

public abstract class AbstractListener extends FPluginObject
{
    public static class ListenerWrapper extends PacketListenerAbstract
    {
        private final AbstractListener listener;

        public ListenerWrapper(AbstractListener protocolListener)
        {
            this.listener = protocolListener;
        }

        @Override
        public void onPacketSend(PacketSendEvent event)
        {
            super.onPacketSend(event);

            this.listener.onPacketSending(event);
        }
    }

    protected AbstractListener()
    {
        this.listenerWrapper = new ListenerWrapper(this);
    }

    private final ListenerWrapper listenerWrapper;

    public ListenerWrapper listenerWrapper()
    {
        return this.listenerWrapper;
    }

    protected abstract void onPacketSending(PacketSendEvent event);
}
