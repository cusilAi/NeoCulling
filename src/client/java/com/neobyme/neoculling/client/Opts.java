package com.neobyme.neoculling.client;

import java.util.ArrayList;
import java.util.List;

/** Every setting lives here. Values are plain ints (booleans are 0/1) so the render path stays cheap. */
public final class Opts {
	public enum Cat {
		GENERAL("General"), DISTANCE("Distance"), OCCLUSION("Occlusion");
		public final String title;
		Cat(String title) { this.title = title; }
	}

	public static final class Option {
		public final String id, label, tip, unit;
		public final Cat cat;
		public final boolean bool;
		public final int def, min, max, step;
		public int value;

		Option(String id, Cat cat, String label, String tip, String unit, boolean bool, int def, int min, int max, int step) {
			this.id = id; this.cat = cat; this.label = label; this.tip = tip; this.unit = unit;
			this.bool = bool; this.def = def; this.min = min; this.max = max; this.step = step;
			this.value = def;
		}

		public void set(int v) { value = Math.max(min, Math.min(max, v)); }

		public void cycle() {
			if (bool) { value ^= 1; return; }
			int v = value + step;
			value = v > max ? min : v;
		}

		public String display() {
			if (bool) return value != 0 ? "ON" : "OFF";
			if (min == 0 && value == 0) return "Off";
			return unit.isEmpty() ? String.valueOf(value) : value + " " + unit;
		}

		public boolean isOn() { return value != 0; }
	}

	public static final List<Option> ALL = new ArrayList<>();

	private static Option bool(String id, Cat c, String label, boolean def, String tip) {
		Option o = new Option(id, c, label, tip, "", true, def ? 1 : 0, 0, 1, 1);
		ALL.add(o);
		return o;
	}

	private static Option num(String id, Cat c, String label, int def, int min, int max, int step, String unit, String tip) {
		Option o = new Option(id, c, label, tip, unit, false, def, min, max, step);
		ALL.add(o);
		return o;
	}

	// ---- General
	public static final Option ENABLED = bool("enabled", Cat.GENERAL, "Culling", true, "Master switch. OFF = vanilla rendering.");
	public static final Option KEEP_GLOWING = bool("keepGlowing", Cat.GENERAL, "Keep glowing entities", true, "Never cull entities with a glow outline.");
	public static final Option KEEP_NAMED = bool("keepNamed", Cat.GENERAL, "Keep named entities", false, "Never cull entities that have a custom name tag.");

	// ---- Distance (0 = unlimited)
	public static final Option MAX_DISTANCE = num("maxDistance", Cat.DISTANCE, "All entities", 0, 0, 256, 16, "blocks", "Hard limit for every entity. Off = no global limit.");
	public static final Option MOB_DIST = num("mobDistance", Cat.DISTANCE, "Mobs", 96, 0, 256, 8, "blocks", "Living entities (mobs, animals).");
	public static final Option PLAYER_DIST = num("playerDistance", Cat.DISTANCE, "Other players", 0, 0, 256, 16, "blocks", "Other players. Off = never cull by distance.");
	public static final Option ITEM_DIST = num("itemDistance", Cat.DISTANCE, "Dropped items", 48, 0, 256, 8, "blocks", "Item entities lying on the ground.");
	public static final Option XP_DIST = num("xpDistance", Cat.DISTANCE, "XP orbs", 32, 0, 256, 8, "blocks", "Experience orbs.");
	public static final Option ARMOR_DIST = num("armorStandDistance", Cat.DISTANCE, "Armor stands", 64, 0, 256, 8, "blocks", "Armor stands.");
	public static final Option FRAME_DIST = num("itemFrameDistance", Cat.DISTANCE, "Item frames", 64, 0, 256, 8, "blocks", "Item frames and glow item frames.");
	public static final Option PROJ_DIST = num("projectileDistance", Cat.DISTANCE, "Projectiles", 96, 0, 256, 8, "blocks", "Arrows, snowballs, etc.");
	public static final Option OTHER_DIST = num("otherDistance", Cat.DISTANCE, "Everything else", 0, 0, 256, 16, "blocks", "Boats, minecarts, and other entities.");

	// ---- Occlusion
	public static final Option OCCLUSION = bool("occlusion", Cat.OCCLUSION, "Hide behind walls", true, "Skip entities with no line of sight (blocked by solid blocks).");
	public static final Option OCC_MIN = num("occlusionMinDistance", Cat.OCCLUSION, "Min distance", 8, 2, 32, 2, "blocks", "Only test entities at least this far away.");
	public static final Option OCC_INTERVAL = num("occlusionInterval", Cat.OCCLUSION, "Re-check every", 2, 1, 20, 1, "ticks", "How often visibility is re-tested. Higher = faster, but more pop-in.");

	// ---- Presets
	private static final Option[] PRESET_OPTS = {
			MAX_DISTANCE, MOB_DIST, ITEM_DIST, XP_DIST, ARMOR_DIST, FRAME_DIST, PROJ_DIST, PLAYER_DIST,
			OCCLUSION, OCC_MIN, OCC_INTERVAL};
	private static final int[] QUALITY  = {0,   0, 96, 64,  96,  96, 128, 0, 0, 8, 2};
	private static final int[] BALANCED = {0,  96, 48, 32,  64,  64,  96, 0, 1, 8, 2};
	private static final int[] LOW_END  = {96, 64, 24, 16,  32,  32,  64, 0, 1, 4, 1};

	public static void preset(String name) {
		int[] v;
		switch (name) {
			case "Off" -> { ENABLED.set(0); return; }
			case "Quality" -> v = QUALITY;
			case "Low-end" -> v = LOW_END;
			default -> v = BALANCED;
		}
		ENABLED.set(1);
		for (int i = 0; i < PRESET_OPTS.length; i++) PRESET_OPTS[i].set(v[i]);
	}

	private Opts() {}
}
