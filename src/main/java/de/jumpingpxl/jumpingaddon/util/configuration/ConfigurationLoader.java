package de.jumpingpxl.jumpingaddon.util.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.main.Source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class ConfigurationLoader {

	private final JumpingAddon jumpingAddon;

	@Inject
	public ConfigurationLoader(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;
	}

	public Optional<JsonObject> getJsonObjectFromUrl(String url, String fileName) {
		InputStreamReader inputStreamReader;
		URLConnection urlConnection;
		BufferedReader bufferedReader;

		try {
			urlConnection = new URL(url + fileName).openConnection();
			urlConnection.setRequestProperty("User-Agent", Source.getUserAgent());
			urlConnection.setReadTimeout(5000);
			urlConnection.setConnectTimeout(5000);
			urlConnection.connect();

			inputStreamReader = new InputStreamReader(urlConnection.getInputStream(),
					StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}

		try {
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(bufferedReader);
			return Optional.of(jsonObject);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public Optional<JsonObject> getJsonObjectFromFile(Path path) {
		if (!Files.exists(path)) {
			return Optional.empty();
		}

		try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(bufferedReader);
			return Optional.of(jsonObject);
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public Optional<JsonObject> getJsonObjectFromResource(String fileName) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
				JumpingAddon.PATH_DATA_FOLDER + fileName);
		if (Objects.isNull(inputStream)) {
			return Optional.empty();
		}

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
				StandardCharsets.UTF_8);

		try {
			JsonObject jsonObject = (JsonObject) new JsonParser().parse(inputStreamReader);
			return Optional.of(jsonObject);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public boolean saveJsonObjectToFile(Path path, JsonObject jsonObject) {
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
			if (!Files.exists(path)) {
				Files.createFile(path);
			}

			bufferedWriter.write(JumpingAddon.GSON.toJson(jsonObject));
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
