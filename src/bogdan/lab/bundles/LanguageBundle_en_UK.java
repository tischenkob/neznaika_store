package bogdan.lab.bundles;

import java.util.ListResourceBundle;

public class LanguageBundle_en_UK extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return resource;
	}
	static final Object[][] resource = new Object[][]{
		{"race", "race"},
		{"name", "name"},
		{"age", "age"},
		{"column.map", "Map"},
		{"birthdate", "birthdate"},
		{"objectAmount", "amount of objects"},
		{"total", "total"},
		{"your", "your"},
		{"add", "add"},
		{"delete", "delete"},
		{"save", "save"},
		{"position", "position"},
		{"", ""},
		{"", ""},
		{"loggedAs", "logged in as"},
		{"or", "or"},
		{"title", "Chubziki"},
		{"tab.columns", "Columns"},
		{"language", "language"},
		{"norespondOne", "SERVER NOT RESPONDING"},
		{"norespondTwo", "if it doesn't respond in 5 seconds, you will be sent back to the login screen"},
		{"browse", "browse"},
		{"import", "import"},
		{"importFile", "import file"},
		{"deleteAll", "delete all"},
		{"deleteOlder", "delete older than"},
		{"shorty", "Shorty"},
		{"lunatic", "Lunatic"},
		{"english", "English"},
		{"estonian", "Estonian"},
		{"russian", "Russian"},
		{"bulgarian", "Bulgarian"},
		{"apply", "apply"},
		{"filter", "Filter"},
		{"", ""},
		{"", ""}
	};
}
