# LargeTextParser

**Постановка задачи:**

Необходимо разработать приложение для Android (Минимальная версия Android - 5.0):

1. Пользователь вводит URL текстового файла, указывает фильтр отбора строк и запускает загрузку (например, нажатием кнопки).
2. Приложение подгружает файл, отбирает строки, удовлетворяющие фильтру, и отображает их в виде списка.


**Принцип отбора строк:**

Предполагается, что текстовый файл является текстовым ANSI-файлом (закладываться на UTF-8 не нужно).

Отбор строк осуществляется по условиям простейшего regexp: (как минимум операторы * и ?):

- символ '*' - последовательность любых символов неограниченной длины;
- символ "?" - один любой символ;
- должны корректно отрабатываться маски: \*Some\*, \*Some, Some\*, \*\*\*\*\*Some\*\*\* - нет никаких ограничений на положение * в маске.


**Результатом поиска должны быть строки, удовлетворяющие маске.**

Например:

1. Маска \*abc\*  отбирает все строки, содержащие abc и начинающиеся и заканчивающиеся любой последовательностью символов.
2. Маска abc* отбирает все строки, начинающиеся с abc и заканчивающиеся любой последовательностью символов.
3. Маска abc? отбирает все строки, начинающиеся с abc и заканчивающиеся любым дополнительным символом.
4. Маска abc отбирает все строки, которые равны этой маске.


**Требования к реализации:**

1. Использование Java или Kotlin - по выбору автора.
2. Обработка данных (парсинг входящих данных) должна осуществляться в отдельном потоке(ах). Не нужно дожидаться полной загрузки.
3. Размер исходного текстового файла (и, возможно, результатов) может быть от сотен мегабайт.
4. Затраты памяти должны быть минимальны (в разумных пределах).
5. Результаты парсинга должны писаться в файл results.log в каталоге приложения.
6. Результат должен быть представлен в виде списка (ListView), который должен наполняться во время парсинга по мере нахождения новых строк, удовлетворяющих условию. Должна быть обеспечена возможность выделить одновременно несколько строк с последующим их копированием в буфер обмена.
7. Интерфейс не должен блокироваться (приводить к ANR) во время скачивания и обработки данных.
8. Код должен быть абсолютно «неубиваемым» и защищённым от ошибок.
