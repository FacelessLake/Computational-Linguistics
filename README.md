# Computational-Linguistics
Tasks for Computational Linguistics course

### Практическое задание No1
<b>Задача</b>: Построить частотный словарь по корпусу текстов.
1. Реализовать морфологический анализ (лемматизацию, определение части речи и морф. признаков)
- можно использовать существующие ресурсы (Zaliz, oDict, OpenCorpora и др.),
- нельзя использовать существующие морфологические анализаторы
(mystem, pymorphy и т.п.).

2. Вход: корпус текстов, тип сортировки.
3. Выход: отсортированный по частоте словарь вида <лемма, ЧР, Freq, TextFreq>

### Практическое задание No2

<b>Задача</b>: Реализовать метод построения конкордансов и вычисление частот совместной встречаемости.
1. Конкорданс строится для произвольной текстовой последовательности
2. Все слова вначале нормализуются - если слово не найдено, оно считается нормализованным
3. Обеспечить сортировку по частоте левого/правого контекста.

Вход: корпус текстов + словарь, фраза, размер окна (n), частотный порог(необ.)

Выход: отсортированные по частоте контексты (длины не более n) фразы

вначале левые, затем правые, затем левые + правые
<л|п|лп, норм_контекст, частота>

### Практическое задание No3

<b>Задача</b>: Реализовать методы извлечения N-грамм и
дополнить частотный словарь N-граммами.
1. Использовать свой морфоанализатор.
2. Извлечь все повторяющиеся более одного раза цепочки
(нормализовать)
3. Отфильтровать на основе оценки устойчивости.
4. Обеспечить поиск N-грамм по слову, вложенных N-грамм
5. Обеспечить сохранение/загрузку итогового предметного словаря.
Вход: корпус
Выход: отсортированный словарь устойчивых N-грамм
<N-грамма, Частота, Текстовая частота, TF-IDF>

### Практическое задание No4
<b>Задача</b>: Реализовать метод семантико-синтаксического анализа
на основе моделей (10-20шт.).
1. Разработать модели для анализ текста. Записать на
формальном языке (json/xml/txt)
2. Реализовать методы поиска в корпусе ЕЯ-фрагментов,
удовлетворяющих моделям.
Вход: корпус, файл с моделями
Выход: списки найденных фрагментов
<N модели, Кол-во вхождений, Фрагменты (каждый с новой строки)>
3. Оценить полноту/точность/F-меру
4. Сопроводить решение документацией.

### Практическое задание No5
<b>Задача</b>: Разработать поисковую систему на основе тезауруса
специализированной области знаний.
1. Использовать свой корпус и словарь (включая N-граммы).
2. Создать тезаурус: разметить наиболее значимые термины словаря
~50шт.: синонимы + общее-частное + ассоц.
3. Реализовать метод поиска в корпусе на основе словаря используя
отношения между терминами.
Вход: поисковые запрос на ЕЯ
Выход: отсортированные по релевантности тексты (фрагменты текста)
<N текста, Pos в тексте, Оценка>
4. Создать корпус запросов: не менее 20 запросов, с распределением
ключевых терминов от 2х до 6т в каждом запросе.
Посчитать полноту/точность/F-меру поиска
