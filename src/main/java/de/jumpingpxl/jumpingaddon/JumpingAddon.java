package de.jumpingpxl.jumpingaddon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import de.jumpingpxl.jumpingaddon.util.AddonLogger;
import de.jumpingpxl.jumpingaddon.util.Settings;
import de.jumpingpxl.jumpingaddon.util.UpdateChecker;
import de.jumpingpxl.jumpingaddon.util.configuration.ConfigurationLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class JumpingAddon extends LabyModAddon implements Module {

	public static final String PATH_DATA_FOLDER = "assets/minecraft/jumpingaddon/data/";
	public static final String LOCAL_CONFIG_PATH = "LabyMod/JumpingAddon2/";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final int VERSION = 1;

	private Injector injector;
	private AddonLogger logger;
	private ConfigurationLoader configurationLoader;
	private UpdateChecker updateChecker;
	private Settings settings;

	private boolean deactivated;

	@Override
	public void configure(Binder binder) {
		binder.bind(JumpingAddon.class).toInstance(this);
	}

	@Override
	public void onEnable() {
		injector = Guice.createInjector(this);
		logger = injector.getInstance(AddonLogger.class);

		System.out.println("JumpingAddon start");
		logger.info("Initializing the JumpingAddon for LabyMod");

		configurationLoader = injector.getInstance(ConfigurationLoader.class);
		updateChecker = injector.getInstance(UpdateChecker.class);
		settings = injector.getInstance(Settings.class);

		if (deactivated) {
			logger.error("Finished initializing the addon with errors; the addon is deactivated");
		} else {
			logger.info("Finished initializing the addon without any errors");
		}
	}

	@Override
	public void loadConfig() {
		settings.loadConfig();
	}

	@Override
	protected void fillSettings(List<SettingsElement> settingsElements) {
		settings.fillSettings(settingsElements);
	}

	public AddonLogger getLogger() {
		return logger;
	}

	public ConfigurationLoader getConfigurationLoader() {
		return configurationLoader;
	}

	public UpdateChecker getUpdateChecker() {
		return updateChecker;
	}

	public Settings getSettings() {
		return this.settings;
	}

	public boolean isDeactivated() {
		return this.deactivated;
	}

	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}
}
