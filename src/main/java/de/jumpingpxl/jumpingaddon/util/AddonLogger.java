package de.jumpingpxl.jumpingaddon.util;

import com.google.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddonLogger {

	private static final Logger LOGGER = Logger.getLogger("JumpingAddon");
	private static final String PREFIX = "[JumpingAddon] ";

	@Inject
	public AddonLogger() {
	}

	public void info(String message) {
		LOGGER.log(Level.INFO, PREFIX + message);
	}

	public void warning(String message) {
		LOGGER.log(Level.WARNING, PREFIX + message);
	}

	public void error(String message) {
		LOGGER.log(Level.SEVERE, PREFIX + message);
	}
}
