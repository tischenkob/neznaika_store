package bogdan.lab.bundles;

import java.util.ListResourceBundle;

public class LanguageBundle_et_EE extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return resource;
	}
	static final Object[][] resource = new Object[][]{
		{"race", "sugu"},
		{"name", "nimi"},
		{"age", "vanus"},
		{"column.map", "kaart"},
		{"birthdate", "sünnikuupäev"},
		{"objectAmount", "objektide arv"},
		{"total", "kokku"},
		{"your", "kokku"},
		{"add", "lisama"},
		{"delete", "kustutage"},
		{"save", "salvestage"},
		{"position", "positsiooni"},
		{"loggedAs", "sisse logitud"},
		{"or", "või"},
		{"title", "Päkapikke"},
		{"tab.columns", "Veerud"},
		{"language", "keel"},
		{"norespondOne", "SERVER EI VASTUTA"},
		{"norespondTwo", "kui see ei reageeri 5 sekundi pärast, saadetakse teid tagasi sisselogimisekraanile"},
		{"browse", "sirvida"},
		{"import", "sissevedu"},
		{"importFile", "impordi fail"},
		{"deleteAll", "kustutada kõik"},
		{"deleteOlder", "kustutada vanemad kui"},
		{"shorty", "Lühike"},
		{"lunatic", "Lunatic"},
		{"english", "Inglise"},
		{"estonian", "Eesti"},
		{"russian", "Vene"},
		{"bulgarian", "Bulgaaria"},
		{"apply", "kohaldatakse"},
		{"filter", "filter"},
		{"", ""},
		{"", ""}
	};
}
