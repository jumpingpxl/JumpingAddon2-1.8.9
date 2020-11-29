package de.jumpingpxl.jumpingaddon.util;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.configuration.Configuration;
import de.jumpingpxl.jumpingaddon.util.configuration.LoadingType;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class Settings {

	private final JumpingAddon jumpingAddon;
	private final Configuration configuration;

	@Inject
	public Settings(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;

		configuration = Configuration.load(jumpingAddon, "config.json", LoadingType.FILE);
		loadConfig();
	}

	private JsonObject getConfig() {
		return jumpingAddon.getConfig();
	}

	private void saveConfig() {
		jumpingAddon.saveConfig();
	}

	public void loadConfig() {

	}

	public void fillSettings(List<SettingsElement> list) {
		list.add(new HeaderElement("§eJumpingAddon v2b" + JumpingAddon.VERSION));
		list.add(new HeaderElement(" "));
	}

	public JumpingAddon getJumpingAddon() {
		return this.jumpingAddon;
	}
}
