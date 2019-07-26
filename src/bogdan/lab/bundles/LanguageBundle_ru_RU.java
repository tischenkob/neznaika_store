package bogdan.lab.bundles;

import java.util.ListResourceBundle;

public class LanguageBundle_ru_RU extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return resource;
	}
	static final Object[][] resource = new Object[][]{
		{"race", "раса"},
		{"name", "имя"},
		{"age", "возраст"},
		{"column.map", "Карта"},
		{"birthdate", "дата"},
		{"objectAmount", "число объектов"},
		{"total", "всего"},
		{"your", "ваших"},
		{"add", "доб."},
		{"delete", "удал."},
		{"save", "сохр."},
		{"position", "позиция"},
		{"loggedAs", "вход выполнен за"},
		{"or", "или"},
		{"title", "Чубзики"},
		{"tab.columns", "Колонки"},
		{"language", "язык"},
		{"norespondOne", "СЕРВЕР НЕ ОТВЕЧАЕТ"},
		{"norespondTwo", "вы вернётесь на начальный экран, если сервер не ответит в течение пяти секунд"},
		{"browse", "искать"},
		{"import", "загрузить"},
		{"importFile", "загрузить файл"},
		{"deleteAll", "удалить всех"},
		{"deleteOlder", "удалить старше чем"},
		{"shorty", "Коротышка"},
		{"lunatic", "Лунатик"},
		{"english", "Английский"},
		{"estonian", "Эстонский"},
		{"russian", "Русский"},
		{"bulgarian", "Болгарский"},
		{"apply", "применить"},
		{"filter", "Фильтровать"},

	};
}
