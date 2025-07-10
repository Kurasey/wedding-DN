document.addEventListener('DOMContentLoaded', function() {
    const LAST_USED_PACK_KEY = 'timeline_last_used_icon_pack';

    const form = document.getElementById('timeline-form');
    const formHeader = document.getElementById('form-header');
    const formSubmitBtn = document.getElementById('form-submit-btn');
    const formCancelBtn = document.getElementById('form-cancel-btn');

    const formId = document.getElementById('form-id');
    const formDisplayOrder = document.getElementById('form-displayOrder');
    const formEventTime = document.getElementById('eventTime');
    const formTitle = document.getElementById('title');
    const formIconNameHiddenInput = document.getElementById('form-iconName');

    const iconPackSelect = document.getElementById('icon-pack-select');
    const iconSelectorContainer = document.getElementById('icon-selector-container');
    const iconSelector = document.getElementById('icon-selector');
    const editButtons = document.querySelectorAll('.btn-edit');

    /**
     * Сбрасывает форму в состояние "Добавления нового элемента".
     */
    function resetForm() {
        form.reset();
        formId.value = '';
        formDisplayOrder.value = '';
        formIconNameHiddenInput.value = '';

        formHeader.textContent = 'Добавить новый пункт';
        formSubmitBtn.textContent = 'Добавить';
        formSubmitBtn.classList.remove('btn-primary');
        formSubmitBtn.classList.add('btn-success');

        formCancelBtn.style.display = 'none';
        iconSelectorContainer.style.display = 'none';
        iconSelector.innerHTML = '';
        // Сбрасываем выбор пака, но не удаляем из localStorage,
        // чтобы при следующем добавлении он снова выбрался.
        if (iconPackSelect) {
            const lastUsedData = localStorage.getItem(LAST_USED_PACK_KEY);
            if (lastUsedData) {
                 const { path } = JSON.parse(lastUsedData);
                 iconPackSelect.value = path;
                 iconPackSelect.dispatchEvent(new Event('change'));
            } else {
                 iconPackSelect.value = '';
            }
        }
    }

    /**
     * Загружает и отображает иконки для выбранного пака.
     * @param {string} packType - 'local' или 'remote'.
     * @param {string} packPath - Путь/название пака.
     * @param {string} [selectedIconUrl=''] - URL иконки, которую нужно выделить.
     */
    function loadIcons(packType, packPath, selectedIconUrl = '') {
        iconSelector.innerHTML = '<div class="spinner-border spinner-border-sm" role="status"></div>';
        iconSelectorContainer.style.display = 'block';

        fetch(`/admin/timeline/icons/${packType}/${encodeURIComponent(packPath)}`)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(iconUrls => {
                iconSelector.innerHTML = ''; // Очищаем спиннер
                if (iconUrls.length === 0) {
                    iconSelector.textContent = 'Иконок в этом паке не найдено.';
                    return;
                }
                iconUrls.forEach(url => {
                    const img = document.createElement('img');
                    img.src = url;
                    img.className = 'icon-selector-item';
                    img.style.width = '32px';
                    img.style.height = '32px';
                    img.dataset.iconUrl = url;

                    if (selectedIconUrl === url) {
                        img.classList.add('selected');
                    }
                    iconSelector.appendChild(img);
                });
            })
            .catch(error => {
                console.error('Error fetching icons:', error);
                iconSelector.textContent = 'Ошибка загрузки иконок.';
            });
    }

    // --- ОБРАБОТЧИКИ СОБЫТИЙ ---

    // 1. Смена пака в выпадающем списке
    if (iconPackSelect) {
        iconPackSelect.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            if (!selectedOption || !selectedOption.value) {
                iconSelectorContainer.style.display = 'none';
                localStorage.removeItem(LAST_USED_PACK_KEY);
                return;
            }
            const packType = selectedOption.dataset.type;
            const packPath = selectedOption.value;

            // Сохраняем выбор пользователя
            localStorage.setItem(LAST_USED_PACK_KEY, JSON.stringify({type: packType, path: packPath}));

            loadIcons(packType, packPath);
        });
    }

    // 2. Клик по иконке в селекторе
    if (iconSelector) {
        iconSelector.addEventListener('click', function(e) {
            if (e.target.tagName === 'IMG') {
                iconSelector.querySelectorAll('img').forEach(img => img.classList.remove('selected'));
                e.target.classList.add('selected');
                formIconNameHiddenInput.value = e.target.dataset.iconUrl;
            }
        });
    }

    // 3. Клик по кнопкам "Редактировать" в таблице
    editButtons.forEach(button => {
        button.addEventListener('click', function() {
            const itemId = this.dataset.id;

            fetch(`/admin/timeline/${itemId}`)
                .then(response => response.json())
                .then(data => {
                    formId.value = data.id;
                    formDisplayOrder.value = data.displayOrder;
                    formEventTime.value = data.eventTime;
                    formTitle.value = data.title;
                    formIconNameHiddenInput.value = data.iconName;

                    formHeader.textContent = `Редактировать: ${data.title}`;
                    formSubmitBtn.textContent = 'Сохранить изменения';
                    formSubmitBtn.classList.remove('btn-success');
                    formSubmitBtn.classList.add('btn-primary');
                    formCancelBtn.style.display = 'inline-block';

                    // Логика пред-выбора пака и иконки
                    const fullIconUrl = data.iconName;
                    if (fullIconUrl && iconPackSelect) {
                        let foundPack = false;
                        for (const option of iconPackSelect.options) {
                            const packType = option.dataset.type;
                            const packPath = option.value;

                            // Проверяем, содержится ли путь пака в URL иконки
                            const searchString = `/${packPath}/`;
                            if (packPath && fullIconUrl.includes(searchString)) {
                                option.selected = true;
                                loadIcons(packType, packPath, fullIconUrl);
                                foundPack = true;
                                break;
                            }
                        }
                        if (!foundPack) {
                             iconPackSelect.value = '';
                             iconSelectorContainer.style.display = 'none';
                        }
                    } else if (iconPackSelect) {
                       iconPackSelect.value = '';
                       iconSelectorContainer.style.display = 'none';
                    }

                    form.scrollIntoView({ behavior: 'smooth' });
                });
        });
    });

    // 4. Клик по кнопке "Отмена"
    if (formCancelBtn) {
        formCancelBtn.addEventListener('click', resetForm);
    }

    // --- ИНИЦИАЛИЗАЦИЯ ПРИ ЗАГРУЗКЕ ---

    /**
     * Восстанавливает последний использованный пак из localStorage.
     */
    function restoreLastUsedPack() {
        if (!iconPackSelect) return;

        const savedPackData = localStorage.getItem(LAST_USED_PACK_KEY);
        if (savedPackData) {
            try {
                const { type, path } = JSON.parse(savedPackData);
                const optionToSelect = Array.from(iconPackSelect.options).find(opt => opt.value === path && opt.dataset.type === type);

                if (optionToSelect) {
                    optionToSelect.selected = true;
                    // Программно вызываем событие 'change', чтобы запустить загрузку иконок
                    iconPackSelect.dispatchEvent(new Event('change'));
                }
            } catch (e) {
                console.error("Failed to parse saved icon pack data", e);
                localStorage.removeItem(LAST_USED_PACK_KEY);
            }
        }
    }

    restoreLastUsedPack();
});