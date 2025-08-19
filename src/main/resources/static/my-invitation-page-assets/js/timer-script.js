// Файл: timer-script.js

// Ждем, пока вся страница (HTML-структура) будет готова к работе
$(function() {

    // 1. Проверяем, существует ли переменная с датой свадьбы из Thymeleaf
    if (typeof weddingDateTimeISO === 'undefined' || !weddingDateTimeISO) {
        console.error("Дата свадьбы (weddingDateTimeISO) не определена. Таймер не может быть запущен.");
        return; // Прекращаем выполнение скрипта, если даты нет
    }

    // 2. Задаем конечную дату и время, создав объект Date из строки ISO
    var targetDate = new Date(weddingDateTimeISO);

    // 3. Находим на странице все необходимые элементы
    var $countdownElement = $('.countdown'); // Элемент для ВЫВОДА таймера
    var $timerWrapper = $countdownElement.closest('.moveBox'); // Контейнер таймера, который будем скрывать/показывать

    // Блоки для разных состояний
    var $templateBlock = $('.count.weddatecontent'); // Блок-ШАБЛОН для таймера
    var $todayBlock = $('.now.weddatecontent'); // Блок "Сегодня свадьба"
    var $marriedBlock = $('.yet.weddatecontent'); // Блок "Мы женаты"

    // Элемент для вывода количества дней после свадьбы
    var $marriedDaysCounter = $marriedBlock.find('strong');

    // 4. Проверяем, существуют ли все элементы, чтобы избежать ошибок
    if ($countdownElement.length === 0 || $templateBlock.length === 0 || $todayBlock.length === 0 || $marriedBlock.length === 0) {
        console.error("Не найдены все необходимые HTML-элементы для таймера.");
        return;
    }

    // 5. Получаем HTML-шаблон для таймера из скрытого блока
    var template = $templateBlock.html();

    // --- Основная логика для определения состояния таймера ---

    var now = new Date();
    var todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    var weddingDayStart = new Date(targetDate.getFullYear(), targetDate.getMonth(), targetDate.getDate());

    // --- Выбор состояния ---

    // СОСТОЯНИЕ 1: Свадьба еще не наступила
    if (now < targetDate) {
        // Скрываем блоки "сегодня" и "после", показываем сам таймер
        $todayBlock.addClass('hide');
        $marriedBlock.addClass('hide');
        $timerWrapper.show(); // Убедимся, что контейнер таймера видим
        $templateBlock.addClass('hide'); // Шаблон всегда должен быть скрыт!

        // Запускаем плагин countdown
        $countdownElement.countdown(targetDate, function(event) {
            // Каждую секунду обновляем HTML таймера
            $(this).html(event.strftime(template));
        }).on('finish.countdown', function() {
            // Когда таймер дошел до нуля:
            $timerWrapper.hide(); // Скрываем таймер
            $todayBlock.removeClass('hide'); // Показываем блок "Сегодня свадьба!"
        });
    }
    // СОСТОЯНИЕ 2: Сегодня день свадьбы
    else if (todayStart.getTime() === weddingDayStart.getTime()) {
        // Скрываем таймер и блок "после", показываем блок "сегодня"
        $timerWrapper.hide();
        $marriedBlock.addClass('hide');
        $todayBlock.removeClass('hide');
    }
    // СОСТОЯНИЕ 3: Свадьба уже прошла
    else {
        // Скрываем таймер и блок "сегодня", показываем блок "после"
        $timerWrapper.hide();
        $todayBlock.addClass('hide');
        $marriedBlock.removeClass('hide');

        // Рассчитываем количество дней, прошедших со дня свадьбы
        var timeDiff = todayStart.getTime() - weddingDayStart.getTime();
        var daysMarried = Math.floor(timeDiff / (1000 * 3600 * 24));

        // Обновляем текст в блоке
        $marriedDaysCounter.text(daysMarried);
    }
});