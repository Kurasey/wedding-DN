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
if (burger && menu) {
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
}

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
            closeMenuIfOpen();
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

// --- Логика для RSVP Модального окна ---
function initializeRsvpModals() {
    const openRsvpModalBtn = document.getElementById('openRsvpModalBtn');
    if (!openRsvpModalBtn) return; // Выход, если кнопки нет

    const rsvpModal = document.getElementById('rsvpModal2');
    const rsvpModalSuccess = document.getElementById('rsvpModal3');
    const closeModalBtn = document.getElementById('closeModal2Btn');
    const closeModalSuccessBtn1 = document.getElementById('closeModal3Btn');
    const closeModalSuccessBtn2 = document.getElementById('closeModal3Btn2');
    const rsvpForm = document.getElementById('rsvpForm2');
    const guestDetailsContainer = document.getElementById('guestDetailsContainer');
    const addGuestBtn = document.getElementById('addGuestBtn');
    const contactPhoneInput = document.getElementById('contactPhone');

    const guestLimitElement = document.getElementById('guest-limit-data');
    const maxGuests = guestLimitElement ? parseInt(guestLimitElement.dataset.maxGuests, 10) : 1;

    function closeModal(modal) {
        if (modal) modal.style.display = "none";
        if (!rsvpModal.style.display || rsvpModal.style.display === "none") {
            document.body.style.overflow = 'auto';
        }
    }

    [closeModalBtn, closeModalSuccessBtn1, closeModalSuccessBtn2].forEach(btn => {
        if (btn) btn.onclick = () => closeModal(btn.closest('.modal'));
    });

    window.onclick = (event) => {
        if (event.target.classList.contains('modal')) closeModal(event.target);
    };
    window.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeModal(rsvpModal);
            closeModal(rsvpModalSuccess);
        }
    });

    openRsvpModalBtn.onclick = async function() {
    const hasResponded = this.dataset.hasResponded === 'true';
    const personalLink = this.dataset.personalLink;

        contactPhoneInput.readOnly = hasResponded;

        if (hasResponded) {
            try {
                const response = await fetch(`/${personalLink}/rsvp/details`);
                if (!response.ok) throw new Error('Не удалось загрузить данные гостей.');
                const guests = await response.json();
                populateGuestDetailsForm(guests);
            } catch (error) {
                console.error("Ошибка при загрузке данных RSVP:", error);
                alert("Не удалось загрузить ваши предыдущие ответы. Пожалуйста, обновите страницу и попробуйте снова.");
                return;
            }
        } else {
            const defaultGuests = Array.from({ length: 1 }, () => ({ willAttend: true }));
            populateGuestDetailsForm(defaultGuests);
        }

        rsvpModal.style.display = "block";
        document.body.style.overflow = 'hidden';
    };

    function populateGuestDetailsForm(guests) {
        guestDetailsContainer.innerHTML = '';
        guests.forEach((guest, index) => {
            const guestEntryHTML = createGuestEntryHTML(index, guest);
            guestDetailsContainer.insertAdjacentHTML('beforeend', guestEntryHTML);
        });
        attachDeleteListeners();
        updateAddGuestButtonVisibility();
    }

    function createGuestEntryHTML(index, guestData = {}) {
        const isAttendingChecked = guestData.willAttend === undefined ? true : guestData.willAttend;
        return `
            <div class="guest-entry" data-index="${index}">
                <input type="hidden" name="guestId" value="${guestData.id || ''}">
                <button type="button" class="btn-delete-guest" title="Удалить этого гостя">×</button>
                <h4>${guestData.id ? `Данные гостя` : (guestDetailsContainer.children.length === 0 ? 'Ваши данные' : `Гость ${guestDetailsContainer.children.length + 1}`)}</h4>
                <div class="form-group">
                    <label for="guestName${index}">ФИО:</label>
                    <input type="text" id="guestName${index}" name="guestName" value="${guestData.name || ''}" required>
                </div>
                <div class="form-group form-check">
                    <input type="checkbox" class="form-check-input" id="willAttend${index}" name="willAttend" ${isAttendingChecked ? 'checked' : ''}>
                    <label class="form-check-label" for="willAttend${index}">Подтверждаю присутствие</label>
                </div>
                <div class="drink-options-container">
                    <p>Предпочтения по напиткам:</p>
                    <div class="drink-options">
                        ${drinkOptionsList.map(drink => `
                            <label>
                                <input type="checkbox" name="guest${index}_drink" value="${drink.displayName}"
                                       ${guestData.drinks && guestData.drinks.includes(drink.displayName) ? 'checked' : ''}>
                                ${drink.displayName}
                            </label>
                        `).join('')}
                    </div>
                </div>
            </div>`;
    }

    function updateAddGuestButtonVisibility() {
        const currentCount = guestDetailsContainer.querySelectorAll('.guest-entry').length;
        addGuestBtn.style.display = (currentCount < maxGuests) ? 'block' : 'none';
    }

    function attachDeleteListeners() {
        guestDetailsContainer.querySelectorAll('.btn-delete-guest').forEach(btn => {
            btn.onclick = function() {
                if (confirm('Вы уверены, что хотите удалить этого гостя из списка?')) {
                    this.closest('.guest-entry').remove();
                    updateAddGuestButtonVisibility();
                }
            };
        });
    }

    addGuestBtn.onclick = function() {
        const currentCount = guestDetailsContainer.querySelectorAll('.guest-entry').length;
        if (currentCount < maxGuests) {
            const newIndex = guestDetailsContainer.lastElementChild ? parseInt(guestDetailsContainer.lastElementChild.dataset.index) + 1 : 0;
            const newGuestHTML = createGuestEntryHTML(newIndex, { willAttend: true });
            guestDetailsContainer.insertAdjacentHTML('beforeend', newGuestHTML);
            attachDeleteListeners();
            updateAddGuestButtonVisibility();
        }
    };

    rsvpForm.onsubmit = async function(event) {
        event.preventDefault();
        const submitButton = rsvpForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        submitButton.textContent = 'Отправка...';

        const guestEntries = guestDetailsContainer.querySelectorAll('.guest-entry');
        const guestsData = [];
        let allFormsValid = true;

        guestEntries.forEach(entry => {
            if (!allFormsValid) return;
            const guestId = entry.querySelector('input[name="guestId"]').value;
            const guestNameInput = entry.querySelector('input[name="guestName"]');
            const guestName = guestNameInput.value.trim();
            const willAttend = entry.querySelector('input[name="willAttend"]').checked;

            if (!guestName) {
                alert(`Пожалуйста, укажите ФИО для одного из гостей.`);
                guestNameInput.focus();
                allFormsValid = false;
                return;
            }

            const entryIndex = entry.dataset.index;
            const selectedDrinks = Array.from(entry.querySelectorAll(`input[name="guest${entryIndex}_drink"]:checked`))
                                        .map(cb => cb.value);

            guestsData.push({
                id: guestId ? parseInt(guestId, 10) : null,
                name: guestName,
                drinks: selectedDrinks,
                willAttend: willAttend
            });
        });

        if (allFormsValid) {
            const rsvpPayload = {
                contactPhone: contactPhoneInput.value,
                guests: guestsData
            };

            const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const headerName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            const headers = { 'Content-Type': 'application/json' };
            headers[headerName] = token;
            const personalLink = openRsvpModalBtn.dataset.personalLink;

            try {
                const response = await fetch(`/${personalLink}/rsvp`, {
                    method: 'POST',
                    headers: headers, // Используем обновленные заголовки
                    body: JSON.stringify(rsvpPayload)
                });

                if (response.ok) {
                    closeModal(rsvpModal);
                    rsvpModalSuccess.style.display = "block";
                    document.body.style.overflow = 'hidden';
                    openRsvpModalBtn.dataset.hasResponded = 'true';
                    const buttonTextSpan = openRsvpModalBtn.querySelector('span');
                    if (buttonTextSpan) buttonTextSpan.textContent = 'Изменить ответ';
                } else {
                    if (response.status === 403) {
                         alert('Ошибка безопасности. Вероятно, ваша сессия истекла. Пожалуйста, обновите страницу и попробуйте снова.');
                    } else {
                        const errorData = await response.json();
                        let errorMessage = "Произошла ошибка. Пожалуйста, проверьте введенные данные.";
                        if (typeof errorData === 'object' && errorData !== null) {
                             errorMessage = Object.values(errorData).join('\n');
                        }
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
    };
}


function initializePreloader() {
    const preloader = document.getElementById('preloader');
    const openEnvelopeImage = document.getElementById('openEnvelopeImage');
    const body = document.body;
    const PRELOADER_SHOWN_KEY = `weddingInvitationEnvelopeShown_${window.location.pathname}`;

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