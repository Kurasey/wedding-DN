// Файл: js/custom-scripts.js

/**
 * Запускает праздничный салют из конфетти.
 * Использует библиотеку confetti.js, которая должна быть подключена на странице.
 */
function triggerCelebrationConfetti() {
    // Вспомогательная функция для получения случайного числа в диапазоне
    function randomInRange(min, max) {
        return Math.random() * (max - min) + min;
    }

    // Запускаем основной салют из центра
    confetti({
        angle: randomInRange(55, 125),
        spread: randomInRange(50, 70),
        particleCount: randomInRange(50, 100),
        origin: { y: 0.6 } // Чуть выше центра по вертикали
    });

    // В старом коде была еще логика для запуска конфетти по бокам,
    // но она зависела от HTML-элементов (.colors .color), которых нет
    // в вашей верстке. Поэтому я оставил только основной, самый красивый эффект.
}

// Пример использования (например, при подтверждении присутствия):
// triggerCelebrationConfetti();

// Файл: js/custom-scripts.js (добавьте этот код после функции конфетти)

// Ждем, пока вся страница будет готова
$(function() {

    // --- ИСПРАВЛЕННЫЙ КОД ДЛЯ РАБОТЫ МЕНЮ ---

    // Находим элементы меню один раз для производительности
    const $menuButton = $('#menu .menuNav');
    // ИСПРАВЛЕНИЕ: Уточняем селектор, чтобы он находил ТОЛЬКО иконку меню, а не музыку
    const $menuIcon = $('#menu .menuNav .menuNavIcon');
    const $dropdown = $('#menu .dropdownMenubox');

    // 1. Обработчик клика по иконке меню (бургеру)
    $menuButton.on('click', function(event) {
        event.stopPropagation();
        $dropdown.toggleClass('hide');
        // Теперь эта строка будет влиять только на одну правильную иконку
        $menuIcon.toggleClass('fa-bars fa-times');
    });

    // 2. Закрываем меню при клике на любой пункт в нем
    $dropdown.on('click', 'a', function() {
        $dropdown.addClass('hide');
        $menuIcon.removeClass('fa-times').addClass('fa-bars');
    });

    // 3. Закрываем меню, если пользователь кликнул в любом другом месте на странице
    $(document).on('click', function(event) {
        if (!$menuButton.is(event.target) && $menuButton.has(event.target).length === 0 &&
            !$dropdown.is(event.target) && $dropdown.has(event.target).length === 0)
        {
            if (!$dropdown.hasClass('hide')) {
                $dropdown.addClass('hide');
                $menuIcon.removeClass('fa-times').addClass('fa-bars');
            }
        }
    });


    // --- КОД ДЛЯ ПЛАВНОЙ ПРОКРУТКИ ---

    // Выбираем все ссылки внутри меню, которые ведут на якоря (#)
    $('#menu a[href^="#"]').on('click', function(event) {
        event.preventDefault();

        const targetId = $(this).attr('href');
        const $target = $(targetId);

        if ($target.length) {
            // Подстраиваем отступ, чтобы меню не перекрывало заголовок секции
            const menuHeight = $('#menu').outerHeight() || 60; // Высота меню, 60px как запасной вариант
            const targetPosition = $target.offset().top - menuHeight;

            $('html, body').animate({
                scrollTop: targetPosition
            }, 800); // 800 миллисекунд для плавной анимации
        }
    });

});