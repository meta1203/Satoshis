package me.meta1203.plugins.satoshis;

import java.util.logging.Logger;

public class Util {
	
	public static final Logger log = Logger.getLogger("Minecraft");
	
	public static double roundTo(double input, int place) {
		return Math.round(input/(10*place))/(10*place);
	}
	
}
