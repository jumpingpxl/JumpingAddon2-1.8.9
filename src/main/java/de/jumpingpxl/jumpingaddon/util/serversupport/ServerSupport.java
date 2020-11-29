package de.jumpingpxl.jumpingaddon.util.serversupport;

import com.google.inject.Inject;
import de.jumpingpxl.jumpingaddon.JumpingAddon;
import de.jumpingpxl.jumpingaddon.util.configuration.Configuration;
import de.jumpingpxl.jumpingaddon.util.configuration.LoadingType;

public class ServerSupport {

	private final JumpingAddon jumpingAddon;
	private final Configuration serverConfiguration;

	@Inject
	public ServerSupport(JumpingAddon jumpingAddon) {
		this.jumpingAddon = jumpingAddon;

		serverConfiguration = Configuration.load(jumpingAddon, "server.json", LoadingType.URL_OR_FILE);
	}
}
