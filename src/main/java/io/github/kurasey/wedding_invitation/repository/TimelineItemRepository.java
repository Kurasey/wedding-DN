package io.github.kurasey.wedding_invitation.repository;

import io.github.kurasey.wedding_invitation.model.TimelineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TimelineItemRepository extends JpaRepository<TimelineItem, Long> {

    List<TimelineItem> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT MAX(t.displayOrder) FROM TimelineItem t")
    Optional<Integer> findMaxDisplayOrder();
}