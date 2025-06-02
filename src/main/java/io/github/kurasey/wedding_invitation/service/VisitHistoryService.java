package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.model.VisitHistoryRecord;
import io.github.kurasey.wedding_invitation.repository.VisitHistoryRecordRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisitHistoryService {

    VisitHistoryRecordRepository historyRecordRepository;

    public VisitHistoryService(VisitHistoryRecordRepository historyRecordRepository) {
        this.historyRecordRepository = historyRecordRepository;
    }

    public VisitHistoryRecord save(VisitHistoryRecord record) {
        return historyRecordRepository.save(record);
    }

    public List<VisitHistoryRecord> historyByPersonalLink(String personalLink) {
        return historyRecordRepository.findByPersonalLink(personalLink);
    }

    public List<VisitHistoryRecord> findAll() {
        return historyRecordRepository.findAll();
    }
}
