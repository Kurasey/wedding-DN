package io.github.kurasey.wedding_invitation.repository;

import io.github.kurasey.wedding_invitation.model.VisitHistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitHistoryRecordRepository extends JpaRepository<VisitHistoryRecord, Long> {

    List<VisitHistoryRecord> findByPersonalLink(String personalLink);
}
