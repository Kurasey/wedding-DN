const burger = document.getElementById('burger');
const menu = document.getElementById('menu');

function toggleMenu() {
  menu.classList.toggle('hidden');
  burger.classList.toggle('hidden');
}

function closeMenuIfOpen() {
  if (!menu.classList.contains('hidden')) {
    menu.classList.add('hidden');
    burger.classList.toggle('hidden');
  }
}

// Открытие/закрытие по кнопке
burger.addEventListener('click', (e) => {
  e.stopPropagation(); // чтобы не сработал document click
  toggleMenu();
});

// Закрытие при клике вне меню
document.addEventListener('click', (e) => {
  if (!menu.contains(e.target) && e.target !== burger) {
    closeMenuIfOpen();
  }
});

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
    const navLinks = document.querySelectorAll('.sticky-nav a[href^="#"]');
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
    if (!openRsvpModalBtn) return; // Выход, если кнопки нет

    const rsvpModal1 = document.getElementById('rsvpModal1');
    const rsvpModal2 = document.getElementById('rsvpModal2');
    const rsvpModal3 = document.getElementById('rsvpModal3');
    const closeModal1Btn = document.getElementById('closeModal1Btn');
    const closeModal2Btn = document.getElementById('closeModal2Btn');
    const closeModal3Btn = document.getElementById('closeModal3Btn');
    const closeModal3Btn2 = document.getElementById('closeModal3Btn2');
    const rsvpForm1 = document.getElementById('rsvpForm1');
    const rsvpForm2 = document.getElementById('rsvpForm2');
    const guestDetailsContainer = document.getElementById('guestDetailsContainer');

    let currentGuestCount = 0;
    let currentContactPhone = '';

    // Открыть первое модальное окно
    openRsvpModalBtn.onclick = function() {
            const hasResponded = openRsvpModalBtn.dataset.hasResponded === 'true';

            if (hasResponded) {
                const userConfirmed = confirm(
                    "Вы уже отправляли ответ. Отправка новой формы полностью заменит предыдущие данные.\n\nПродолжить?"
                );
                if (!userConfirmed) {
                    return;
                }
            }
        rsvpModal1.style.display = "block";
        document.body.style.overflow = 'hidden';
    }

    // Функции закрытия модальных окон
    function closeModal(modal) {
        if (modal) modal.style.display = "none";
        if (!isAnyModalOpen()) {
            document.body.style.overflow = 'auto';
        }
    }

    function isAnyModalOpen() {
        return (rsvpModal1 && rsvpModal1.style.display === "block") ||
               (rsvpModal2 && rsvpModal2.style.display === "block") ||
               (rsvpModal3 && rsvpModal3.style.display === "block");
    }

    [closeModal1Btn, closeModal2Btn, closeModal3Btn, closeModal3Btn2].forEach(btn => {
        if (btn) btn.onclick = () => closeModal(btn.closest('.modal'));
    });

    window.onclick = (event) => {
        if (event.target.classList.contains('modal')) closeModal(event.target);
    }
    window.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && isAnyModalOpen()) {
             if (rsvpModal1.style.display === 'block') closeModal(rsvpModal1);
             if (rsvpModal2.style.display === 'block') closeModal(rsvpModal2);
             if (rsvpModal3.style.display === 'block') closeModal(rsvpModal3);
        }
    });

    // Обработка первой формы RSVP
    const guestLimitElement = document.getElementById('guest-limit-data');
    const maxGuests = guestLimitElement ? parseInt(guestLimitElement.dataset.maxGuests, 10) : 1;
    const guestCountInput = document.getElementById('guestCount');
    if (guestCountInput) {
        guestCountInput.max = maxGuests;
    }

    rsvpForm1.onsubmit = function(event) {
        event.preventDefault();
        currentContactPhone = document.getElementById('contactPhone').value;
        currentGuestCount = guestCountInput ? parseInt(guestCountInput.value, 10) : 1;

        if (currentGuestCount > 0 && currentGuestCount <= maxGuests && currentContactPhone) {
            closeModal(rsvpModal1);
            populateGuestDetailsForm(currentGuestCount);
            rsvpModal2.style.display = "block";
            document.body.style.overflow = 'hidden';
        } else if (currentGuestCount > maxGuests) {
            alert(`Пожалуйста, укажите не более ${maxGuests} гостей. Для большего количества свяжитесь с нами напрямую.`);
        } else {
            alert("Пожалуйста, заполните все поля корректно.");
        }
    }

    // Генерация полей для информации о гостях
    function populateGuestDetailsForm(count) {
        guestDetailsContainer.innerHTML = '';
        for (let i = 1; i <= count; i++) {
            const guestEntryDiv = document.createElement('div');
            guestEntryDiv.classList.add('guest-entry');
            guestEntryDiv.innerHTML = `
                <h4>${count === 1 ? 'Ваши данные' : `Гость ${i}`}</h4>
                <div class="form-group">
                    <label for="guestName${i}">ФИО:</label>
                    <input type="text" id="guestName${i}" name="guestName${i}" required>
                </div>
                <div class="drink-options-container">
                    <p>Предпочтения по напиткам:</p>
                    <div class="drink-options">
                        ${drinkOptionsList.map(drink => `
                            <label>
                                <input type="checkbox" name="guest${i}_drink" value="${drink.displayName}">
                                ${drink.displayName}
                            </label>
                        `).join('')}
                    </div>
                </div>
            `;
            guestDetailsContainer.appendChild(guestEntryDiv);
        }
        const firstFioInput = guestDetailsContainer.querySelector('input[type="text"]');
        if (firstFioInput) firstFioInput.focus();
    }

    // Обработка второй формы RSVP и отправка на сервер
    rsvpForm2.onsubmit = async function(event) {
        event.preventDefault();
        const submitButton = rsvpForm2.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        submitButton.textContent = 'Отправка...';

        const guestsData = [];
        let allGuestFormsValid = true;

        for (let i = 1; i <= currentGuestCount; i++) {
            const guestNameInput = document.getElementById(`guestName${i}`);
            const guestName = guestNameInput.value.trim();
            if (!guestName) {
                alert(`Пожалуйста, укажите ФИО для ${currentGuestCount === 1 ? 'вас' : `Гостя ${i}`}.`);
                guestNameInput.focus();
                allGuestFormsValid = false;
                break;
            }
            const selectedDrinks = Array.from(document.querySelectorAll(`input[name="guest${i}_drink"]:checked`))
                                        .map(cb => cb.value);
            guestsData.push({ name: guestName, drinks: selectedDrinks });
        }

        if (allGuestFormsValid) {
            const rsvpPayload = {
                contactPhone: currentContactPhone,
                guests: guestsData
            };

            const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const headerName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            const headers = {
                'Content-Type': 'application/json'
            };
            headers[headerName] = token; // Добавляем CSRF токен в заголовки

            const personalLink = window.location.pathname.split('/')[1];
            try {
                const response = await fetch(`/${personalLink}/rsvp`, {
                    method: 'POST',
                    headers: headers, // Используем обновленные заголовки
                    body: JSON.stringify(rsvpPayload)
                });

                if (response.ok) {
                    closeModal(rsvpModal2);
                    rsvpModal3.style.display = "block";
                    openRsvpModalBtn.textContent = "Ваш ответ получен!";
                    openRsvpModalBtn.disabled = true;
                    rsvpForm1.reset();
                    guestDetailsContainer.innerHTML = '';
                } else {
                    if (response.status === 403) {
                         alert('Ошибка безопасности (CSRF). Пожалуйста, обновите страницу и попробуйте снова.');
                    } else {
                        const errorData = await response.json();
                        const errorMessage = Object.values(errorData).join('\n');
                        alert(`Ошибка валидации:\n${errorMessage}`);
                    }
                }
            } catch (error) {
                console.error('Ошибка отправки RSVP:', error);
                alert('Произошла ошибка при отправке данных. Пожалуйста, попробуйте снова или свяжитесь с нами напрямую.');
            }
        }
        submitButton.disabled = false;
        submitButton.textContent = 'Отправить Подтверждение';
    }
}


function initializePreloader() {
    const preloader = document.getElementById('preloader');
    const openEnvelopeImage = document.getElementById('openEnvelopeImage');
    const body = document.body;
    const PRELOADER_SHOWN_KEY = 'weddingInvitationEnvelopeShown_v1';

    if (!preloader || !openEnvelopeImage) return;

    if (sessionStorage.getItem(PRELOADER_SHOWN_KEY)) {
        preloader.style.display = 'none';
        body.classList.remove('preloader-active');
        return;
    }

    body.classList.add('preloader-active');

    const hidePreloader = () => {
        preloader.classList.add('preloader-hidden');
        body.classList.remove('preloader-active');
        sessionStorage.setItem(PRELOADER_SHOWN_KEY, 'true');
        setTimeout(() => { preloader.style.display = 'none'; }, 700);
    };

    openEnvelopeImage.addEventListener('click', hidePreloader);
    openEnvelopeImage.onerror = () => {
        console.error("Failed to load preloader image.");
        hidePreloader();
    };
}

// --- ИНИЦИАЛИЗАЦИЯ ---
document.addEventListener('DOMContentLoaded', () => {
    initializePreloader();

    // Используем переменную weddingDateTimeISO, определенную в HTML
    if (typeof weddingDateTimeISO !== 'undefined') {
        const weddingDate = new Date(weddingDateTimeISO).getTime();
        startCountdown(weddingDate);
    }

    setCurrentYear();
    smoothScroll();
    initializeRsvpModals();
});