package com.wuballiance.ld33;

public class SaveFile {
	public static String[] names;
	public static boolean[] comps;
	public static int[] shots;
	public static boolean muted;

	static {
		names = new String[] { "Maps/FirstSteps", "Maps/AliveFinally", "Maps/Conserve", "Maps/Corners", "Maps/Rounds", "Maps/HollowPoint",
				"Maps/StoppingForce", "Maps/Loading", "Maps/Map1", "Maps/OneAndOnly", "Maps/Katamari", "Maps/Compass", "Maps/Spiral", "Maps/Zig",
				"Maps/Clockwise", "Maps/Split", "Maps/ChainReaction", "Maps/HarrisMap1" };
		comps = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
		shots = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		muted = false;
	}

	public static boolean isCompleted(String name) {
		int found = -1;

		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				found = i;
				break;
			}
		}
		return comps[found];
	}

	public static int getHighScore(String name) {
		int found = -1;

		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				found = i;
				break;
			}
		}
		return shots[found];
	}

	public static void setHighScore(String name, int shot) {
		int found = -1;

		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				found = i;
				break;
			}
		}
		shots[found] = shot;
	}
}
