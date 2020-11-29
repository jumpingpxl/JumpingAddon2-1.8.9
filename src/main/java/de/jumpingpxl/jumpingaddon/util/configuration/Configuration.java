package de.jumpingpxl.jumpingaddon.util.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.AddonLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class Configuration {

	private final JumpingAddon jumpingAddon;
	private final ConfigurationLoader loader;
	private final AddonLogger logger;
	private final JsonObject jsonObject;
	private final Configuration parent;
	private Path localPath;

	private Configuration(JumpingAddon jumpingAddon, Configuration parent, JsonElement jsonElement) {
		this.jumpingAddon = jumpingAddon;
		this.loader = jumpingAddon.getConfigurationLoader();
		this.logger = jumpingAddon.getLogger();
		this.parent = parent;

		jsonObject = jsonElement.getAsJsonObject();
	}

	private Configuration(JumpingAddon jumpingAddon, String fileName, LoadingType loadingType) {
		this.jumpingAddon = jumpingAddon;
		this.loader = jumpingAddon.getConfigurationLoader();
		this.logger = jumpingAddon.getLogger();

		parent = null;
		Optional<JsonObject> optional;
		switch (loadingType) {
			case URL:
				optional = fromUrl(fileName);
				break;
			case URL_OR_RESOURCE:
				optional = fromUrlOrResource(fileName);
				break;
			case URL_OR_FILE:
				optional = fromUrlOrFile(fileName);
				break;
			case FILE:
				optional = fromFile(fileName);
				break;
			default:
				optional = Optional.empty();
				break;
		}

		if (!optional.isPresent()) {
			logger.warning(
					"Configuration -> Loading the Configuration " + fileName + "returned an empty Optional");
			jsonObject = new JsonObject();
		} else {
			jsonObject = optional.get();
		}
	}

	public static Configuration load(JumpingAddon jumpingAddon, String fileName,
	                                 LoadingType loadingType) {
		return new Configuration(jumpingAddon, fileName, loadingType);
	}

	public Configuration getParent() {
		return parent;
	}

	public JsonElement get(String memberName) {
		if (!has(memberName)) {
			throw new IllegalArgumentException("The requested member " + memberName + " doesn't exist");
		}

		return jsonObject.get(memberName);
	}

	public Configuration getChild(String memberName) {
		return new Configuration(jumpingAddon, this, get(memberName));
	}

	public ConfigArray getArray(String memberName) {
		return ConfigArray.fromMemberName(this, memberName);
	}

	public String getString(String memberName) {
		return get(memberName).getAsString();
	}

	public int getInteger(String memberName) {
		return get(memberName).getAsInt();
	}

	public double getDouble(String memberName) {
		return get(memberName).getAsDouble();
	}

	public boolean getBoolean(String memberName) {
		return get(memberName).getAsBoolean();
	}

	public boolean has(String memberName) {
		return jsonObject.has(memberName);
	}

	public boolean isNull(String memberName) {
		return !has(memberName) || get(memberName).isJsonNull();
	}

	public void setString(String memberName, String value) {
		jsonObject.addProperty(memberName, value);
	}

	public void setInteger(String memberName, int value) {
		jsonObject.addProperty(memberName, value);
	}

	public void setDouble(String memberName, double value) {
		jsonObject.addProperty(memberName, value);
	}

	public void setBoolean(String memberName, boolean value) {
		jsonObject.addProperty(memberName, value);
	}

	public void set(String memberName, JsonElement jsonElement) {
		jsonObject.add(memberName, jsonElement);
	}

	public void save() {
		if (Objects.nonNull(parent)) {
			parent.save();
		}

		Objects.requireNonNull(localPath, "Path cannot be null");

		jumpingAddon.getConfigurationLoader().saveJsonObjectToFile(localPath, jsonObject);
	}

	private Path getLocalPath(String fileName) {
		if (Objects.isNull(localPath)) {
			localPath = Paths.get(JumpingAddon.LOCAL_CONFIG_PATH, fileName);
		}

		return localPath;
	}

	private Optional<JsonObject> fromUrl(String fileName) {
		String mainUrl = jumpingAddon.getUpdateChecker().getConfigUrl();
		logger.info("Configuration -> Loading the file " + fileName + " from the main url " + mainUrl);
		Optional<JsonObject> fromMainUrl = loader.getJsonObjectFromUrl(mainUrl, fileName);
		if (fromMainUrl.isPresent()) {
			return fromMainUrl;
		}

		logger.info(
				"Configuration -> The main url " + mainUrl + "gave no or an invalid response for the file "
						+ fileName + ". Trying the backup url...");
		String backupUrl = jumpingAddon.getUpdateChecker().getBackupConfigUrl();
		Optional<JsonObject> fromBackupUrl = loader.getJsonObjectFromUrl(backupUrl, fileName);
		if (fromBackupUrl.isPresent()) {
			return fromBackupUrl;
		}

		logger.info(
				"Configuration -> The backup url " + backupUrl + "gave no or an invalid response for the "
						+ "file " + fileName + ". The addon might not work now");
		return Optional.empty();
	}

	private Optional<JsonObject> fromUrlOrFile(String fileName) {
		Optional<JsonObject> optionalFromUrl = fromUrl(fileName);
		if (optionalFromUrl.isPresent()) {
			return optionalFromUrl;
		}

		logger.info("Configuration -> Trying to load the file " + fileName + " from local file...");
		return fromFile(fileName);
	}

	private Optional<JsonObject> fromUrlOrResource(String fileName) {
		Optional<JsonObject> optionalFromUrl = fromUrl(fileName);
		if (optionalFromUrl.isPresent()) {
			return optionalFromUrl;
		}

		logger.info("Configuration -> Trying to load the file " + fileName + " from resources...");
		Optional<JsonObject> optionalFromResource = loader.getJsonObjectFromResource(fileName);
		if (optionalFromResource.isPresent()) {
			return optionalFromResource;
		}

		logger.error("Configuration -> The resource " + fileName + " could not be found; The addon "
				+ "might not work properly now");
		return Optional.empty();
	}

	private Optional<JsonObject> fromFile(String fileName) {
		logger.info("Configuration -> Loading the file " + fileName + " from local file");
		Optional<JsonObject> optionalFromFile = loader.getJsonObjectFromFile(getLocalPath(fileName));
		if (optionalFromFile.isPresent()) {
			return optionalFromFile;
		}

		logger.warning(
				"Configuration -> File " + fileName + " could not be found. Creating default config.");
		Optional<JsonObject> optionalFromResource = loader.getJsonObjectFromResource(fileName);
		if (!optionalFromResource.isPresent()) {
			logger.error("Configuration -> Resource " + fileName + " could not be found; no default "
					+ "config was created. The addon might not work properly now");
			return Optional.empty();
		}

		JsonObject fromResource = optionalFromResource.get();
		if (loader.saveJsonObjectToFile(getLocalPath(fileName), fromResource)) {
			logger.info("Configuration -> The default config " + fileName + " was saved successfully.");
		} else {
			logger.warning("Configuration -> The default config " + fileName + " could not be saved. "
					+ "The addon will still work");
		}

		return optionalFromResource;
	}

	public static class ConfigArray {

		private final Configuration parent;
		private final JsonArray jsonArray;

		private ConfigArray(Configuration parent, JsonArray jsonArray) {
			this.parent = parent;
			this.jsonArray = jsonArray;
		}

		private ConfigArray(JsonArray jsonArray) {
			this(null, jsonArray);
		}

		private ConfigArray(Configuration parent, String memberName) {
			this(parent, parent.get(memberName).getAsJsonArray());
		}

		public static ConfigArray fromJsonArray(Configuration parent, JsonArray jsonArray) {
			return new ConfigArray(parent, jsonArray);
		}

		public static ConfigArray fromJsonArray(JsonArray jsonArray) {
			return new ConfigArray(jsonArray);
		}

		public static ConfigArray fromMemberName(Configuration parent, String memberName) {
			return new ConfigArray(parent, memberName);
		}

		public Configuration getParent() {
			return parent;
		}

		public JsonArray getJsonArray() {
			return jsonArray;
		}

		public void add(JsonElement jsonElement) {
			jsonArray.add(jsonElement);
		}

		public void forEach(Consumer<Configuration> consumer) {
			jsonArray.forEach(jsonElement -> consumer.accept(
					new Configuration(parent.jumpingAddon, parent, jsonElement)));
		}
	}
}