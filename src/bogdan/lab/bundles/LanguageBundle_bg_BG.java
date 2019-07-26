package bogdan.lab.bundles;

import java.util.ListResourceBundle;

public class LanguageBundle_bg_BG extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return resource;
	}

	static final Object[][] resource = new Object[][]{
		{"race", "раса"},
		{"name", "име"},
		{"age", "възраст"},
		{"column.map", "карта"},
		{"birthdate", "рождена дата"},
		{"objectAmount", "количество обекти"},
		{"total", "обща"},
		{"your", "твоят"},
		{"add", "добави"},
		{"delete", "изтрий"},
		{"save", "спасяване"},
		{"position", "позиция"},
		{"loggedAs", "влезли като"},
		{"or", "или"},
		{"title", "Чубзы"},
		{"tab.columns", "Колони"},
		{"language", "език"},
		{"norespondOne", "СЪРВЪРЪТ НЕ ОТКЛЮЧВА"},
		{"norespondTwo", "ако не отговори в рамките на 5 секунди, ще бъдете върнати на екрана за вход"},
		{"browse", "паса"},
		{"import", "внос"},
		{"importFile", "файл за импортиране"},
		{"deleteAll", "изтриване на всички"},
		{"deleteOlder", "изтриване по - стари от"},
		{"shorty", "Дребосък"},
		{"lunatic", "Лунатик"},
		{"english", "Английски"},
		{"estonian", "Естонски"},
		{"russian", "Руски"},
		{"bulgarian", "Български"},
		{"apply", "приложи"},
		{"filter", "Филтъра"},
		{"", ""},
		{"", ""}
	};
}
