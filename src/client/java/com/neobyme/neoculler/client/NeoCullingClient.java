package com.neobyme.neoculler.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.neobyme.neoculler.NeoCulling;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class NeoCullingClient implements ClientModInitializer {
private static final int KEY_I = 73;
private static final int KEY_LEFT_CTRL = 341;
private static final int KEY_RIGHT_CTRL = 345;

private static KeyMapping openSettings;

@Override
public void onInitializeClient() {
Cfg.load();

KeyMapping.Category category = KeyMapping.Category.register(NeoCulling.id("main"));
openSettings = KeyMappingHelper.registerKeyMapping(
new KeyMapping("key.neoculling.settings", KEY_I, category));

ClientTickEvents.END_CLIENT_TICK.register(mc -> {
while (openSettings.consumeClick()) {
if (ctrlDown(mc)) mc.gui.setScreen(new NeoSettingsScreen(null));
}
});
}

private static boolean ctrlDown(Minecraft mc) {
return InputConstants.isKeyDown(KEY_LEFT_CTRL)
|| InputConstants.isKeyDown(KEY_RIGHT_CTRL);
}
}
