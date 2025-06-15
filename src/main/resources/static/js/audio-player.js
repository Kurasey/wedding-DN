document.addEventListener('DOMContentLoaded', function() {
    const player = document.getElementById('audioPlayer');
    const toggleBtn = document.getElementById('musicToggle');
    const hintArrow = document.getElementById('music-hint-arrow');

    if (!toggleBtn || !player || !hintArrow) {
        return;
    }

    let hasAutoplaySucceeded = false;

    // Функция для динамического позиционирования стрелки
    const positionHintArrow = () => {
        // Получаем координаты кнопки
        const buttonRect = toggleBtn.getBoundingClientRect();

        // Позиционируем стрелку
        // top: Выравниваем по центру кнопки
        hintArrow.style.top = (buttonRect.top + buttonRect.height / 2 - hintArrow.offsetHeight / 2) + 'px';
        // left: Размещаем слева от кнопки с небольшим отступом
        hintArrow.style.left = (buttonRect.left - hintArrow.offsetWidth - 15) + 'px';
    };

    const showHint = () => {
        // СНАЧАЛА позиционируем, ПОТОМ показываем
        positionHintArrow();
        hintArrow.classList.remove('hidden');
        hintArrow.classList.add('visible', 'pulsing');
    };

    const hideHint = () => {
        hintArrow.classList.remove('visible', 'pulsing');
        hintArrow.classList.add('hidden');
    };

    const playMusic = () => {
        hideHint();
        return player.play();
    };

    const pauseMusic = () => {
        player.pause();
    };

    player.addEventListener('play', () => {
        toggleBtn.classList.add('playing');
        hasAutoplaySucceeded = true;
        hideHint();
    });

    player.addEventListener('pause', () => {
        toggleBtn.classList.remove('playing');
    });

    toggleBtn.addEventListener('click', function(event) {
        event.stopPropagation();
        removeInteractionListeners();

        if (player.paused) {
            playMusic();
        } else {
            pauseMusic();
        }
    });

    const handleFirstInteraction = () => {
        if (hasAutoplaySucceeded) {
            removeInteractionListeners();
            return;
        }

        playMusic().then(() => {
            removeInteractionListeners();
        }).catch(error => {
            console.warn("Autoplay was prevented. Showing hint.", error.message);
            showHint();
        });
    };

    const removeInteractionListeners = () => {
        window.removeEventListener('click', handleFirstInteraction);
        window.removeEventListener('scroll', handleFirstInteraction);
        window.removeEventListener('touchstart', handleFirstInteraction);
        // Также перестаем отслеживать ресайз, когда подсказка больше не нужна
        window.removeEventListener('resize', positionHintArrow);
    };

    // Добавляем слушатель на изменение размера окна,
    // чтобы пересчитывать позицию стрелки, если она видима
    window.addEventListener('resize', () => {
        if (hintArrow.classList.contains('visible')) {
            positionHintArrow();
        }
    });

    window.addEventListener('click', handleFirstInteraction);
    window.addEventListener('scroll', handleFirstInteraction);
    window.addEventListener('touchstart', handleFirstInteraction);
});