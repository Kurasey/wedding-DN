document.addEventListener('DOMContentLoaded', function () {
    const calendarContainer = document.getElementById('wedding-calendar-2025-09');
    const calendarSectionToObserve = document.getElementById('calendar-highlight-section');

    if (!calendarContainer || !calendarSectionToObserve) {
        console.warn('Элементы календаря не найдены на странице.');
        return;
    }

function generateWeddingCalendar(targetElement, year, monthIndex, highlightDay) {
        const monthName = new Date(year, monthIndex).toLocaleString('ru-RU', { month: 'long' });
        const daysInMonth = new Date(year, monthIndex + 1, 0).getDate();
        // getDay() 0-воскресенье, 6-суббота. Нам нужен 0-понедельник.
        let firstDayOfWeek = new Date(year, monthIndex, 1).getDay();
        if (firstDayOfWeek === 0) firstDayOfWeek = 6; // Воскресенье -> 6
        else firstDayOfWeek -= 1; // Пн(1)->0, Вт(2)->1 ... Сб(6)->5

        let html = `<div class="calendar-title">${monthName.charAt(0).toUpperCase() + monthName.slice(1)} ${year}</div>`;
        const dayNames = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
        dayNames.forEach(day => {
            html += `<div class="calendar-header">${day}</div>`;
        });

        for (let i = 0; i < firstDayOfWeek; i++) {
            html += `<div class="calendar-empty-day"></div>`;
        }

        for (let day = 1; day <= daysInMonth; day++) {
            let cellClasses = "calendar-day";
            let dayContent = `<span class="calendar-day-number">${day}</span>`;

            if (day === highlightDay) {
                cellClasses += " wedding-day-cell";
                // Добавляем SVG сердечка именно в эту ячейку
                dayContent += `

                    <div class="heart-container" id="wedding-heart-svg-wrapper">
                        <svg class="heart-svg" viewBox="0 0 100 100">
                            <path class="heart-path" d="M50,20 C60,10 80,15 80,30 C80,45 65,60 50,70 C35,60 20,45 20,30 C20,15 40,10 50,20 Z" />
                        </svg>
                    </div>`;
            }
            html += `<div class="${cellClasses}" data-day="${day}">${dayContent}</div>`;
        }
       const calendarLinkHref = `${window.location.pathname}/event.ics`;
       html += `<a href="${calendarLinkHref}" class="calendar-footer-link" download="wedding_event.ics">Добавить в календарь</a>`;

        targetElement.innerHTML = html;
    }

    generateWeddingCalendar(calendarContainer, year, monthJs, weddingDay);

    const observerCallback = (entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const heartWrapper = document.getElementById('wedding-heart-svg-wrapper');
                if (heartWrapper) {
                    const heart = heartWrapper.querySelector('.heart-path');
                    const pathLength = heart.getTotalLength();

                    // Сбрасываем анимацию перед повторным запуском
                    heart.style.animation = 'none';
                    heart.style.strokeDasharray = pathLength;
                    heart.style.strokeDashoffset = pathLength;

                    // Запускаем анимацию после обновления стилей
                    requestAnimationFrame(() => {
                        heart.style.animation = 'draw 3s ease-in-out forwards, beat 1.5s ease infinite 3s';
                    });
                }
            }
        });
    };

    const calendarObserver = new IntersectionObserver(observerCallback, {
        root: null,
        threshold: 0.5
    });

    calendarObserver.observe(calendarSectionToObserve);
});
