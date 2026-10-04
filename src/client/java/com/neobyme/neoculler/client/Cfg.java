package com.neobyme.neoculler.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neobyme.neoculler.NeoCulling;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Cfg {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("neoculling.json");
	}

	public static void load() {
		Path p = file();
		if (!Files.exists(p)) { save(); return; }
		try {
			JsonObject o = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
			for (Opts.Option opt : Opts.ALL) {
				if (o.has(opt.id)) opt.set(o.get(opt.id).getAsInt());
			}
		} catch (Exception e) {
			NeoCulling.LOGGER.warn("Could not read neoculling.json, using defaults", e);
		}
	}

	public static void save() {
		try {
			JsonObject o = new JsonObject();
			for (Opts.Option opt : Opts.ALL) o.addProperty(opt.id, opt.value);
			Files.createDirectories(file().getParent());
			Files.writeString(file(), GSON.toJson(o));
		} catch (Exception e) {
			NeoCulling.LOGGER.warn("Could not save neoculling.json", e);
		}
	}

	private Cfg() {}
}
