package com.neobyme.neoculler.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Opened with Ctrl + I. Tabs on top, two columns of buttons; click a button to change it. */
public class NeoSettingsScreen extends Screen {
	private final Screen parent;
	private Opts.Cat tab = Opts.Cat.GENERAL;

	public NeoSettingsScreen(Screen parent) {
		super(Component.literal("NeoCuller Settings"));
		this.parent = parent;
	}

	private static Component label(Opts.Option o) {
		return Component.literal(o.label + ": ")
				.append(Component.literal(o.display()).withStyle(o.isOn() ? ChatFormatting.GREEN : ChatFormatting.RED));
	}

	@Override
	protected void init() {
		// tabs
		int tabs = Opts.Cat.values().length;
		int tw = Math.min(100, (this.width - 20) / tabs - 4);
		int tx = this.width / 2 - ((tw + 4) * tabs) / 2;
		int i = 0;
		for (Opts.Cat c : Opts.Cat.values()) {
			Component name = Component.literal(c == tab ? "> " + c.title + " <" : c.title);
			addRenderableWidget(Button.builder(name, b -> { tab = c; rebuildWidgets(); })
					.bounds(tx + i * (tw + 4), 10, tw, 20).build());
			i++;
		}

		// options for the current tab
		int colW = Math.min(150, (this.width - 24) / 2);
		int x0 = this.width / 2 - colW - 2;
		int y = 40, col = 0;
		for (Opts.Option o : Opts.ALL) {
			if (o.cat != tab) continue;
			addRenderableWidget(Button.builder(label(o), b -> { o.cycle(); Cfg.save(); b.setMessage(label(o)); })
					.bounds(x0 + col * (colW + 4), y, colW, 20)
					.tooltip(Tooltip.create(Component.literal(o.tip)))
					.build());
			if (++col == 2) { col = 0; y += 24; }
		}
		if (col == 1) y += 24;

		// presets + stats on the General tab
		if (tab == Opts.Cat.GENERAL) {
			y += 8;
			String[] presets = {"Off", "Quality", "Balanced", "Low-end"};
			int p = 0;
			for (String name : presets) {
				addRenderableWidget(Button.builder(Component.literal("Preset: " + name), b -> {
							Opts.preset(name); Cfg.save(); rebuildWidgets();
						})
						.bounds(x0 + (p % 2) * (colW + 4), y + (p / 2) * 24, colW, 20).build());
				p++;
			}
			y += 56;
			addRenderableWidget(Button.builder(Component.literal("Entities culled this session: " + Stats.culled + " (tap to refresh)"),
							b -> rebuildWidgets())
					.bounds(x0, y, colW * 2 + 4, 20).build());
		}

		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
				.bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());
	}

	@Override
	public void onClose() {
		Cfg.save();
		if (this.minecraft != null) this.minecraft.gui.setScreen(parent);
	}
}
