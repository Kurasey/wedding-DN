        // Функция ymaps.ready() будет вызвана, когда
        // загрузятся все компоненты API, а также когда будет готово DOM-дерево.
        ymaps.ready(init);
function init(){
            // Создание карты.
            var myMap = new ymaps.Map("map", {
                // Координаты центра карты (из HTML)
                center: mapCenter,
                // Уровень масштабирования (из HTML)
                zoom: mapZoom,
                // Отключаем лишние кнопки
                controls: ['zoomControl']
            });

            // Создаем метку
            var myPlacemark = new ymaps.Placemark(myMap.getCenter(), {
                // Название места (из HTML)
                hintContent: mapVenueName,
                balloonContent: 'Ждем вас здесь!'
            }, {
                // Опции.
                // Необходимо указать данный тип макета.
                iconLayout: 'default#image',
                // Своё изображение иконки метки.
                iconImageHref: '/images/geo.png',
                // Размеры метки.
                iconImageSize: [40, 40],
                // Смещение левого верхнего угла иконки относительно
                // её "ножки" (точки привязки).
                iconImageOffset: [-20, -38]
            });

            // Добавляем метку на карту
            myMap.geoObjects.add(myPlacemark);

            // Запрещаем менять масштаб колесиком мыши
            myMap.behaviors.disable('scrollZoom');
        }