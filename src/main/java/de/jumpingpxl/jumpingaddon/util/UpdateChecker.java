package de.jumpingpxl.jumpingaddon.util;

import com.google.inject.Inject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.configuration.Configuration;
import de.jumpingpxl.jumpingaddon.util.configuration.LoadingType;

import java.util.Objects;

public class UpdateChecker {

	private static final String UPDATE_FILE = "updates.json";

	private final JumpingAddon jumpingAddon;
	private String configUrl;
	private String backupConfigUrl;
	private int latestVersion;
	private int latestConfig;

	@Inject
	public UpdateChecker(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
		checkForUpdates();
	}

	public void checkForUpdates() {
		jumpingAddon.getLogger().info("UpdateChecker -> Checking for updates...");
		checkForUpdates(Configuration.load(jumpingAddon, UPDATE_FILE, LoadingType.URL_OR_RESOURCE));
	}

	private void checkForUpdates(Configuration configuration) {
		latestConfig = configuration.getInteger("currentConfigVersion");
		configUrl = configuration.getString("currentConfigUrl");
		backupConfigUrl = configuration.getString("backupConfigUrl");

		int newestVersion = configuration.getInteger("currentVersion");
		if (JumpingAddon.VERSION < newestVersion) {
			jumpingAddon.getLogger().info("UpdateChecker -> New version found: v" + newestVersion);
			latestVersion = newestVersion;
			//TODO: Startup Notification

			//TODO: Auto Update
		} else {
			jumpingAddon.getLogger().info("UpdateChecker -> No new version found.");
		}
	}

	public void downloadAddon() {

	}

	public String getConfigUrl() {
		if (Objects.isNull(configUrl)) {
			checkForUpdates(Configuration.load(jumpingAddon, UPDATE_FILE, LoadingType.URL_OR_RESOURCE));
		}

		return configUrl;
	}

	public String getBackupConfigUrl() {
		return backupConfigUrl;
	}

	public int getLatestVersion() {
		return latestVersion;
	}

	public int getLatestConfig() {
		return latestConfig;
	}
}
