package me.meta1203.plugins.satoshis;

public class Util {
	
	public static double roundTo(double input, int place) {
		return Math.round(input/(10*place))/(10*place);
	}
	
}
