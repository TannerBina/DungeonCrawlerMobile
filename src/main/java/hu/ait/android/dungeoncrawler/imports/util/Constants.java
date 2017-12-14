package hu.ait.android.dungeoncrawler.imports.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
	public static final boolean DEBUG = false;
	
	public static final String LAMBDA_DELIMINATOR = "__";
	public static final String CHAR_ID_TAG = "CHAR_ID";
	public static final String NULL = "NULL";

	public static final ArrayList<String> CASTING_CLASSES = new ArrayList<>(
			Arrays.asList("Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer",
					"Warlock", "Wizard"));
}
