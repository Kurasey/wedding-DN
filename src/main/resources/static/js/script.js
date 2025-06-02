// Таймер обратного отсчета
function startCountdown(targetDate) {
    const countdownElement = document.getElementById('countdown');
    if (!countdownElement) return;

    const daysEl = document.getElementById('days');
    const hoursEl = document.getElementById('hours');
    const minutesEl = document.getElementById('minutes');
    const secondsEl = document.getElementById('seconds');

    function updateCountdown() {
        const now = new Date().getTime();
        const distance = targetDate - now;

        if (distance < 0) {
            countdownElement.innerHTML = "<p style='font-size: 1.5em;'>Этот день уже настал!</p>";
            if (typeof interval !== 'undefined') clearInterval(interval);
            return;
        }

        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        daysEl.innerText = String(days).padStart(2, '0');
        hoursEl.innerText = String(hours).padStart(2, '0');
        minutesEl.innerText = String(minutes).padStart(2, '0');
        secondsEl.innerText = String(seconds).padStart(2, '0');
    }

    const interval = setInterval(updateCountdown, 1000);
    updateCountdown(); 
}

// Установка текущего года в футере
function setCurrentYear() {
    const yearElement = document.getElementById('currentYear');
    if (yearElement) {
        yearElement.textContent = new Date().getFullYear();
    }
}

// Плавный скролл для навигации
function smoothScroll() {
    const navLinks = document.querySelectorAll('.sticky-nav a');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                const navHeightElement = document.querySelector('.sticky-nav');
                const navHeight = navHeightElement ? navHeightElement.offsetHeight : 0;
                const elementPosition = targetElement.getBoundingClientRect().top;
                const offsetPosition = elementPosition + window.pageYOffset - navHeight;

                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// --- Логика для RSVP Модальных окон ---
function initializeRsvpModals() {
    const openRsvpModalBtn = document.getElementById('openRsvpModalBtn');
    const rsvpModal1 = document.getElementById('rsvpModal1');
    const rsvpModal2 = document.getElementById('rsvpModal2');
    const closeModal1Btn = document.getElementById('closeModal1Btn');
    const closeModal2Btn = document.getElementById('closeModal2Btn');
    const rsvpForm1 = document.getElementById('rsvpForm1');
    const rsvpForm2 = document.getElementById('rsvpForm2');
    const guestDetailsContainer = document.getElementById('guestDetailsContainer');

    let currentGuestCount = 0;
    let currentContactPhone = '';

    const drinkOptionsList = [
        { id: 'cognac', label: 'Коньяк' },
        { id: 'white_wine_dry', label: 'Вино белое сухое' },
        { id: 'red_wine_semisweet', label: 'Вино красное полусладкое' },
        { id: 'vodka', label: 'Водка' },
        { id: 'champagne', label: 'Шампанское' },
        { id: 'non_alcoholic', label: 'Безалкогольные напитки (соки, вода, лимонады)' }
    ];

    // Открыть первое модальное окно
    if (openRsvpModalBtn) {
        openRsvpModalBtn.onclick = function() {
            rsvpModal1.style.display = "block";
            document.body.style.overflow = 'hidden'; // Блокируем скролл основной страницы
        }
    }

    // Функции закрытия модальных окон
    function closeModal(modal) {
        if (modal) {
            modal.style.display = "none";
        }
        // Разблокируем скролл только если оба модальных окна закрыты
        if ((!rsvpModal1 || rsvpModal1.style.display === "none") && 
            (!rsvpModal2 || rsvpModal2.style.display === "none")) {
            document.body.style.overflow = 'auto';
        }
    }

    if (closeModal1Btn) {
        closeModal1Btn.onclick = function() {
            closeModal(rsvpModal1);
        }
    }
    if (closeModal2Btn) {
        closeModal2Btn.onclick = function() {
            closeModal(rsvpModal2);
        }
    }

    // Закрыть модальные окна по клику вне их контента
    window.onclick = function(event) {
        if (event.target == rsvpModal1) {
            closeModal(rsvpModal1);
        }
        if (event.target == rsvpModal2) {
            closeModal(rsvpModal2);
        }
    }
    // Закрытие по клавише Esc
    window.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            if (rsvpModal1 && rsvpModal1.style.display === 'block') {
                closeModal(rsvpModal1);
            }
            if (rsvpModal2 && rsvpModal2.style.display === 'block') {
                closeModal(rsvpModal2);
            }
        }
    });


    // Обработка первой формы RSVP
    if (rsvpForm1) {
        rsvpForm1.onsubmit = function(event) {
            event.preventDefault();
            currentGuestCount = parseInt(document.getElementById('guestCount').value);
            currentContactPhone = document.getElementById('contactPhone').value;

            if (currentGuestCount > 0 && currentGuestCount <= 10 && currentContactPhone) { // Ограничение на 10 гостей для примера
                closeModal(rsvpModal1);
                populateGuestDetailsForm(currentGuestCount);
                rsvpModal2.style.display = "block";
                document.body.style.overflow = 'hidden'; // Убедимся, что скролл заблокирован
            } else if (currentGuestCount > 10) {
                alert("Пожалуйста, укажите не более 10 гостей. Для большего количества свяжитесь с нами напрямую.");
            }
            else {
                alert("Пожалуйста, заполните все поля корректно.");
            }
        }
    }

    // Генерация полей для информации о гостях
    function populateGuestDetailsForm(count) {
        guestDetailsContainer.innerHTML = ''; // Очищаем предыдущие поля

        for (let i = 1; i <= count; i++) {
            const guestEntryDiv = document.createElement('div');
            guestEntryDiv.classList.add('guest-entry');

            const guestTitle = document.createElement('h4');
            guestTitle.textContent = (count === 1) ? `Ваши данные` : `Гость ${i}`;
            guestEntryDiv.appendChild(guestTitle);

            // Поле ФИО
            const fioFormGroup = document.createElement('div');
            fioFormGroup.classList.add('form-group');
            const fioLabel = document.createElement('label');
            fioLabel.setAttribute('for', `guestName${i}`);
            fioLabel.textContent = 'ФИО:';
            const fioInput = document.createElement('input');
            fioInput.setAttribute('type', 'text');
            fioInput.setAttribute('id', `guestName${i}`);
            fioInput.setAttribute('name', `guestName${i}`);
            fioInput.required = true;
            fioFormGroup.appendChild(fioLabel);
            fioFormGroup.appendChild(fioInput);
            guestEntryDiv.appendChild(fioFormGroup);

            // Поля для напитков
            const drinksContainer = document.createElement('div');
            drinksContainer.classList.add('drink-options-container');
            const drinksHeader = document.createElement('p');
            drinksHeader.textContent = 'Предпочтения по напиткам:';
            drinksContainer.appendChild(drinksHeader);
            
            const drinksOptionsDiv = document.createElement('div');
            drinksOptionsDiv.classList.add('drink-options');

            drinkOptionsList.forEach(drink => {
                const drinkOptionLabel = document.createElement('label');
                const drinkCheckbox = document.createElement('input');
                drinkCheckbox.setAttribute('type', 'checkbox');
                drinkCheckbox.setAttribute('name', `guest${i}_drink_${drink.id}`);
                drinkCheckbox.setAttribute('value', drink.label); // Отправляем читаемое название
                
                drinkOptionLabel.appendChild(drinkCheckbox);
                drinkOptionLabel.appendChild(document.createTextNode(` ${drink.label}`));
                drinksOptionsDiv.appendChild(drinkOptionLabel);
            });
            drinksContainer.appendChild(drinksOptionsDiv);
            guestEntryDiv.appendChild(drinksContainer);

            guestDetailsContainer.appendChild(guestEntryDiv);
        }
        // Фокус на первое поле ФИО
        const firstFioInput = guestDetailsContainer.querySelector('input[type="text"]');
        if (firstFioInput) {
            firstFioInput.focus();
        }
    }

    // Обработка второй формы RSVP
    if (rsvpForm2) {
        rsvpForm2.onsubmit = function(event) {
            event.preventDefault();
            
            const guestsData = [];
            let allGuestFormsValid = true;

            for (let i = 1; i <= currentGuestCount; i++) {
                const guestNameInput = document.getElementById(`guestName${i}`);
                const guestName = guestNameInput.value.trim();

                if (!guestName) {
                    alert(`Пожалуйста, укажите ФИО для ${ (currentGuestCount === 1) ? 'вас' : 'Гостя ' + i}.`);
                    guestNameInput.focus();
                    allGuestFormsValid = false;
                    break; 
                }

                const selectedDrinks = [];
                document.querySelectorAll(`input[name^="guest${i}_drink_"]:checked`).forEach(checkbox => {
                    selectedDrinks.push(checkbox.value);
                });

                guestsData.push({
                    guestNumber: i,
                    name: guestName,
                    drinks: selectedDrinks.length > 0 ? selectedDrinks : ["Не указаны"]
                });
            }

            if (allGuestFormsValid) {
                console.log("--- RSVP Подтверждение ---");
                console.log("Контактный телефон:", currentContactPhone);
                console.log("Общее количество гостей:", currentGuestCount);
                console.log("Информация по гостям:", guestsData);

                // Здесь должна быть логика отправки данных на сервер (AJAX/Fetch)
                // Пример: sendRsvpData({ phone: currentContactPhone, guests: guestsData });

                closeModal(rsvpModal2);
                alert("Спасибо! Ваше подтверждение и предпочтения приняты.\nМы с нетерпением ждем встречи с вами!");

                // Очистка форм и переменных
                rsvpForm1.reset();
                guestDetailsContainer.innerHTML = ''; 
                currentGuestCount = 0;
                currentContactPhone = '';
            }
        }
    }
}
// Вызываем функцию инициализации модальных окон после загрузки DOM (перенесено в DOMContentLoaded)

function initializePreloader() {
    const preloader = document.getElementById('preloader');
    const openEnvelopeImage = document.getElementById('openEnvelopeImage');
    const body = document.body;

        // Ключ для localStorage, чтобы запомнить, что прелоадер уже был показан
        const PRELOADER_SHOWN_KEY = 'weddingInvitationEnvelopeShown_v1'; // Добавил _v1 для уникальности, если понадобится сбросить для всех

    if (!preloader || !openEnvelopeImage) {
        console.warn("Preloader elements not found. Skipping preloader initialization.");
        body.classList.remove('preloader-active'); // Убедимся, что скролл разрешен, если нет прелоадера
        return;
    }

        // Проверяем, был ли прелоадер уже показан этому пользователю
    if (localStorage.getItem(PRELOADER_SHOWN_KEY)) {
        // Если да, сразу скрываем его и разблокируем скролл основной страницы
        preloader.style.display = 'none'; // Сразу скрываем, без анимации
        body.classList.remove('preloader-active');
        return; // Выходим из функции, так как прелоадер показывать не нужно
    }
    

    // Изначально добавляем класс для блокировки скролла
    body.classList.add('preloader-active');

    openEnvelopeImage.addEventListener('click', () => {
        preloader.classList.add('preloader-hidden');
        
        // Убираем класс блокировки скролла
        body.classList.remove('preloader-active');

        // Устанавливаем флаг в localStorage, что прелоадер был показан
        localStorage.setItem(PRELOADER_SHOWN_KEY, 'true');

        // Через некоторое время можно полностью убрать прелоадер из DOM, если нужно
        setTimeout(() => {
            if (preloader.parentNode) { // Проверяем, существует ли еще элемент в DOM
                 preloader.style.display = 'none'; // Для гарантии, если transition не сработает
            }
        }, 700); // Должно быть равно или больше времени transition в CSS (opacity 0.7s)
    });

    // Обработка случая, если изображение конверта не загрузилось
    openEnvelopeImage.onerror = function() {
        console.error("Failed to load envelope image for preloader.");
        // Можно скрыть прелоадер и разблокировать скролл, если картинка не загрузилась
        preloader.classList.add('preloader-hidden');
        body.classList.remove('preloader-active');
        // Также устанавливаем флаг, чтобы не пытаться показать снова при ошибке
        localStorage.setItem(PRELOADER_SHOWN_KEY, 'true');
         setTimeout(() => {
            if (preloader.parentNode) {
                 preloader.style.display = 'none';
            }
        }, 700);
    };
}

// --- ИНИЦИАЛИЗАЦИЯ ---
document.addEventListener('DOMContentLoaded', () => {
    initializePreloader(); // <--- ВЫЗЫВАЕМ ПРЕЛОАДЕР ПЕРВЫМ
    // ❗❗❗ ВАЖНО: Установите ДАТУ и ВРЕМЯ вашей свадьбы! ❗❗❗
    // Формат: 'Месяц ДД, ГГГГ ЧЧ:ММ:СС' (Месяц на английском)
    const weddingDateString = 'September 09, 2025 15:00:00'; // <--- ЗАМЕНИТЕ НА ВАШУ ДАТУ И ВРЕМЯ
    
    if (weddingDateString === '[ЗАМЕНИТЕ_НА_ДАТУ_СВАДЬБЫ]' || weddingDateString.includes("ЗАМЕНИТЕ") || !new Date(weddingDateString).getTime()) {
        console.warn("Пожалуйста, установите корректную дату свадьбы в script.js для работы таймера!");
        const countdownElement = document.getElementById('countdown');
        if (countdownElement) {
            countdownElement.innerHTML = "<p style='font-size: 1em; color: yellow;'>Установите дату свадьбы в script.js для таймера</p>";
        }
    } else {
        const weddingDate = new Date(weddingDateString).getTime();
        startCountdown(weddingDate);
    }
    
    setCurrentYear();
    smoothScroll();
    initializeRsvpModals(); // <--- ДОБАВЬТЕ ЭТУ СТРОКУ
});

