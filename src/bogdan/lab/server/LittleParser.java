package bogdan.lab.server;

import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.humanoid.Lunatic;
import bogdan.lab.humanoid.Shorty;
import com.google.gson.Gson;

public class LittleParser {
	public static String get(String in, String str) {
		String result = "";
		int ind = in.indexOf(str) + str.length();
		try {
			result = in.substring(ind, in.indexOf(",", ind));
		} catch (StringIndexOutOfBoundsException ex) {
			result = in.substring(ind, in.indexOf("}", ind));
		}
		result = result.replaceAll("\"", "");
		result = result.replaceAll(":", "");
		result = result.replaceAll("}", "");
		return result;
	}

	public static Humanoid fromJson(String str) {
		str = str.replaceAll("\\s", "");
		String race = get(str, "race").trim().toLowerCase();
		Gson gson = new Gson();
		if (race.equals("lunatic") || race.equals("лунатик"))
			return gson.fromJson(str, Lunatic.class);
		if (race.equals("shorty") || race.equals("коротышка"))
			return gson.fromJson(str, Shorty.class);
		else System.out.println("Unknown race");
		return null;
	}
}
