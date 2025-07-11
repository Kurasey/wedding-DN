package io.github.kaurami.wems.service;

import io.github.kaurami.wems.model.TimelineItem;
import io.github.kaurami.wems.repository.TimelineItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimelineItemService {

    private final TimelineItemRepository repository;

    public TimelineItemService(TimelineItemRepository repository) {
        this.repository = repository;
    }

    public List<TimelineItem> getAllTimelineItems() {
        return repository.findAllByOrderByDisplayOrderAsc();
    }

    public Optional<TimelineItem> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TimelineItem save(TimelineItem item) {
        if (item.getId() == null) {
            int maxOrder = repository.findMaxDisplayOrder().orElse(-1);
            item.setDisplayOrder(maxOrder + 1);
        }
        return repository.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
        reorderAllItems();
    }

    @Transactional
    public void moveUp(Long id) {
        List<TimelineItem> items = getAllTimelineItems();
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                swapOrder(items.get(i), items.get(i - 1));
                break;
            }
        }
    }

    @Transactional
    public void moveDown(Long id) {
        List<TimelineItem> items = getAllTimelineItems();
        for (int i = 0; i < items.size() - 1; i++) {
            if (items.get(i).getId().equals(id)) {
                swapOrder(items.get(i), items.get(i + 1));
                break;
            }
        }
    }

    private void swapOrder(TimelineItem item1, TimelineItem item2) {
        int tempOrder = item1.getDisplayOrder();
        item1.setDisplayOrder(item2.getDisplayOrder());
        item2.setDisplayOrder(tempOrder);
        repository.save(item1);
        repository.save(item2);
    }

    @Transactional
    public void reorderAllItems() {
        List<TimelineItem> items = repository.findAllByOrderByDisplayOrderAsc();
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setDisplayOrder(i);
            repository.save(items.get(i));
        }
    }
}