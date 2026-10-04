package com.neobyme.neoculling.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.neobyme.neoculling.NeoCulling;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class NeoCullingClient implements ClientModInitializer {
	private static final int KEY_I = 73;             // GLFW_KEY_I
	private static final int KEY_LEFT_CTRL = 341;    // GLFW_KEY_LEFT_CONTROL
	private static final int KEY_RIGHT_CTRL = 345;   // GLFW_KEY_RIGHT_CONTROL

	private static KeyMapping openSettings;

	@Override
	public void onInitializeClient() {
		Cfg.load();

		KeyMapping.Category category = KeyMapping.Category.register(NeoCulling.id("main"));
		// Key is "I"; the Ctrl part is checked below, so the shortcut is Ctrl + I.
		openSettings = KeyBindingHelper.registerKeyBinding(
				new KeyMapping("key.neoculling.settings", KEY_I, category));

		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			// keybinds only fire while no screen is open
			while (openSettings.consumeClick()) {
				if (ctrlDown(mc)) mc.setScreen(new NeoSettingsScreen(null));
			}
		});
	}

	private static boolean ctrlDown(Minecraft mc) {
		return InputConstants.isKeyDown(mc.getWindow(), KEY_LEFT_CTRL)
				|| InputConstants.isKeyDown(mc.getWindow(), KEY_RIGHT_CTRL);
	}
}
