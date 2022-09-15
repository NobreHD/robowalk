package tech.saturns.robowalk;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.*;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.*;

public class Main implements ModInitializer {
	public static ClientPlayerEntity player;
	public static boolean enabled = false;
	public static boolean debug = false;

	@Override
	public void onInitialize() {
		LiteralCommandNode<FabricClientCommandSource> robowalk = DISPATCHER.register(literal("robowalk")
			.then(literal("toggle").executes(ctx -> {
				setPlayer(ctx);
				Main.enabled = !Main.enabled;
				Main.sendMsg("Robowalk is now " + (enabled ? "enabled" : "disabled"));
				return SINGLE_SUCCESS;
			})).then(literal("on").executes(ctx -> {
				setPlayer(ctx);
				Main.enabled = true;
				Main.sendMsg("Robowalk is now enabled");
				return SINGLE_SUCCESS;
			})).then(literal("off").executes(ctx -> {
				setPlayer(ctx);
				Main.enabled = false;
				Main.sendMsg("Robowalk is now disabled");
				return SINGLE_SUCCESS;
			})).then(literal("debug").executes(ctx -> {
				setPlayer(ctx);
				Main.debug = !Main.debug;
				Main.sendMsg("Debug is now " + (debug ? "enabled" : "disabled"));
				return SINGLE_SUCCESS;
			})).then(literal("help").executes(ctx -> {
				setPlayer(ctx);
				Main.sendMsg("§6Commands:");
				Main.sendMsg("§6/robowalk [on/off/toggle]§r - Turn on/off Robowalk");
				Main.sendMsg("§6/robowalk debug§r - Toggles Debug");
				Main.sendMsg("§6/robowalk help§r - Shows this help message (._. )");
				return SINGLE_SUCCESS;
			})).executes(ctx -> {
				setPlayer(ctx);
				Main.sendMsg("Robowalk v1.0 by Saturn5V (modified by NobreHD)");
				Main.sendMsg("Status: " + (Main.enabled ? "Enabled" : "Disabled") + (Main.debug ? " (Debug)" : ""));
				return SINGLE_SUCCESS;
			})
		);
		DISPATCHER.register(literal("rw").redirect(robowalk));
	}

	public static BaseText getPrefix(){
		return new LiteralText("§8[§4Robowalk§8]§r ");
	}

	public static Text format(String text){
		BaseText prefix = getPrefix();
		prefix.append(new LiteralText(text));
		return prefix;
	}

	public static void sendMsg(String text){
		player.sendMessage(format(text), false);
	}

	public static void setPlayer(CommandContext<FabricClientCommandSource> ctx){
		if (Main.player != null && Main.player.equals(MinecraftClient.getInstance().player)) return;
		Main.player = MinecraftClient.getInstance().player;
	}
}
