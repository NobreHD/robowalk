package tech.saturns.robowalk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.text.Text;
import tech.saturns.robowalk.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    MinecraftClient instance = MinecraftClient.getInstance();

    @Inject(at = @At("HEAD"), method = "sendPacket", cancellable = true)
    private void onSendPacket(Packet<?> rawpacket, CallbackInfo callback) {
        if (rawpacket instanceof PlayerMoveC2SPacket packet) {
            if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround || packet instanceof PlayerMoveC2SPacket.Full) {
                if (Main.enabled) {
                    callback.cancel();
                    double x = Math.round(packet.getX(0) * 100.0) / 100.0; //round packets as best we can
                    double z = Math.round(packet.getZ(0) * 100.0) / 100.0;


                    if (Main.debug) Main.sendMsg("Sent [X/Z]: " + x + ":" + z); //Main.debug log

                    long dx = ((long) (x * 1000)) % 10; //simulate the check that liveoverflow runs
                    long dz = ((long) (z * 1000)) % 10;
                    if (Main.debug) Main.sendMsg("Calculated [X/Z]: " + dx + ":" + dz);


                    if (dx != 0 || dz != 0) {
                        if (Main.debug)
                            Main.sendMsg("§4Found packet [DX/DZ] != 0, Modification Failed!, Aborting!!"); //drop these weird packets that sometimes get through
                        return;
                    }

                    Packet<?> clone;

                    if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
                        clone = new PlayerMoveC2SPacket.PositionAndOnGround(x, packet.getY(0), z, packet.isOnGround());
                    } else {
                        clone = new PlayerMoveC2SPacket.Full(x, packet.getY(0), z, packet.getYaw(0), packet.getPitch(0), packet.isOnGround());
                    }


                    instance.player.networkHandler.getConnection().send(clone);
                }
            }
        }
        if (rawpacket instanceof VehicleMoveC2SPacket packet) {
            if (Main.enabled) {
                callback.cancel();
                double x = Math.round(packet.getX() * 100.0) / 100.0; //round packets as best we can
                double z = Math.round(packet.getZ() * 100.0) / 100.0;


                if (Main.debug) Main.sendMsg("Sent [Vehicle] [X/Z]: " + x + ":" + z); //Main.debug log

                long dx = ((long) (x * 1000)) % 10; //simulate the check that liveoverflow runs
                long dz = ((long) (z * 1000)) % 10;
                if (Main.debug) Main.sendMsg("Calculated [Vehicle] [X/Z]: " + dx + ":" + dz);


                if (dx != 0 || dz != 0) {
                    if (Main.debug)
                        Main.sendMsg("§4Found packet [Vehicle] [DX/DZ] != 0, Modification Failed!, Aborting!!"); //drop these weird packets that sometimes get through
                    return;
                }

                Entity vehicle = instance.player.getVehicle();

                vehicle.setPos(x, packet.getY(), z);

                VehicleMoveC2SPacket movepacket = new VehicleMoveC2SPacket(vehicle);

                instance.player.networkHandler.getConnection().send(movepacket);
            }
        }
    }
}
