package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.model.VisitHistoryRecord;
import io.github.kurasey.wedding_invitation.repository.VisitHistoryRecordRepository;
import org.springframework.data.domain.Sort; // <-- ИМПОРТ
import org.springframework.stereotype.Service; // <-- ИЗМЕНЕНО на Service

import java.util.List;

@Service
public class VisitHistoryService {

    private final VisitHistoryRecordRepository historyRecordRepository;

    public VisitHistoryService(VisitHistoryRecordRepository historyRecordRepository) {
        this.historyRecordRepository = historyRecordRepository;
    }

    public VisitHistoryRecord save(VisitHistoryRecord record) {
        return historyRecordRepository.save(record);
    }

    public List<VisitHistoryRecord> historyByPersonalLink(String personalLink) {
        return historyRecordRepository.findByPersonalLink(personalLink, Sort.by(Sort.Direction.DESC, "visitedAt"));
    }

    public List<VisitHistoryRecord> findAll() {
        return historyRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "visitedAt"));
    }

    public long countAll() {
        return historyRecordRepository.count();
    }
}