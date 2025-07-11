package io.github.kaurami.wems.repository;

import io.github.kaurami.wems.model.VisitHistoryRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitHistoryRecordRepository extends JpaRepository<VisitHistoryRecord, Long> {

    List<VisitHistoryRecord> findByPersonalLink(String personalLink, Sort sort);
}