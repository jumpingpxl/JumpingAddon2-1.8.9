package de.jumpingpxl.jumpingaddon.util.configuration;

import com.google.gson.JsonElement;

public class ConfigurationValue {

	private final Configuration configuration;
	private final String memberName;
	private final Object defaultValue;
	private Object value;

	private ConfigurationValue(Configuration configuration, String memberName, Object defaultValue) {
		this.configuration = configuration;
		this.memberName = memberName;
		this.defaultValue = defaultValue;
	}

	public static ConfigurationValue create(Configuration configuration, String memberName,
	                                        Object defaultValue) {
		return new ConfigurationValue(configuration, memberName, defaultValue);
	}

	public Configuration getParent() {
		return configuration;
	}

	public String getMemberName() {
		return memberName;
	}

	public JsonElement getMember() {
		return configuration.get(memberName);
	}

	public String getAsString() {
		return getMember().getAsString();
	}

	public int getAsInt() {
		return getMember().getAsInt();
	}

	public boolean getAsBoolean() {
		return getMember().getAsBoolean();
	}
}
