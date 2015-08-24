package com.wuballiance.ld33;

public class SaveFile {
	public static String[] names;
	public static boolean[] comps;
	public static int[] shots;
	public static boolean muted;
	
	static
	{
		names = new String[] { "TestMap", "Map1", "Map2", "HarrisMap1" };
		comps = new boolean[] { false, false, false, false };
		shots = new int[] { -1, -1, -1, -1 };
		muted = false;
	}
	
	public static boolean isCompleted(String name)
	{
		int found = -1;
		
		for (int i = 0; i < names.length; i++)
		{
			if (names[i].equals(name))
			{
				found = i;
				break;
			}
		}
		return comps[found];
	}
}
