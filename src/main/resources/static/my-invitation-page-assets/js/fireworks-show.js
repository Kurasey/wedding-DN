function launchSingleFirework() {
  confetti({
    zIndex: 3000,
    // Количество частиц
    particleCount: Math.floor(Math.random() * 50) + 75, // от 75 до 125 частиц

    // Разброс на 360 градусов для эффекта взрыва
    spread: 360,

    // Начальная скорость
    startVelocity: 30,

    // Частицы замедляются после "взрыва"
    decay: 0.92,

    // Гравитация, чтобы они падали вниз
    gravity: 0.8,

    // Снос в случайную сторону
    drift: Math.random() - 0.5,

    // Время жизни частиц
    ticks: 300,

    // Точка старта - случайная по X и Y
    origin: {
      x: Math.random(),
      // Y делаем случайным, но не слишком низко, чтобы было красиво
      y: Math.random() - 0.2
    },

    // Яркие цвета, как у фейерверка
    colors: ['#26ccff', '#a25afd', '#ff5e7e', '#88ff5a', '#fcff42', '#ffa62d', '#ff36ff'],

    // Масштаб частиц
    scalar: 1.5
  });
}

/**
 * Запускает серию фейерверков.
 * @param {number} count - Количество залпов.
 * @param {number} interval - Интервал между залпами в миллисекундах.
 */
function startFireworksShow(count = 9, interval = 450) {
  let burst = 0;

  const timer = setInterval(function() {
    launchSingleFirework();
    burst++;

    if (burst >= count) {
      clearInterval(timer);
    }
  }, interval);
}