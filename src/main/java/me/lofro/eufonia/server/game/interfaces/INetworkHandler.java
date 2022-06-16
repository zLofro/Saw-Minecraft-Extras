package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

public interface INetworkHandler {
    void sendPacket(Packet<?> packet, Entity ifSees);
}
