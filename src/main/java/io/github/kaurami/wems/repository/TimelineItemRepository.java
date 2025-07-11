package io.github.kaurami.wems.repository;

import io.github.kaurami.wems.model.TimelineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TimelineItemRepository extends JpaRepository<TimelineItem, Long> {

    List<TimelineItem> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT MAX(t.displayOrder) FROM TimelineItem t")
    Optional<Integer> findMaxDisplayOrder();
}