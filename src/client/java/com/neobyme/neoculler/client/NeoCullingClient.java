package com.neobyme.neoculler.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.neobyme.neoculler.NeoCulling;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;

public class NeoCullingClient implements ClientModInitializer {
	private static KeyMapping openSettings;

	@Override
	public void onInitializeClient() {
		Cfg.load();

		KeyMapping.Category category = KeyMapping.Category.register(NeoCulling.id("main"));
		// 73 = GLFW_KEY_I. The Ctrl part is checked below, so the shortcut is Ctrl + I.
		openSettings = KeyMappingHelper.registerKeyMapping(
				new KeyMapping("key.neoculling.settings", InputConstants.Type.KEYSYM, 73, category));

		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			while (openSettings.consumeClick()) {
				if (Screen.hasControlDown() && mc.screen == null) {
					mc.setScreen(new NeoSettingsScreen(null));
				}
			}
		});
	}
}
