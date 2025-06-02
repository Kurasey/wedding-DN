package io.github.kurasey.wedding_invitation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class VisitHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String personalLink;

    @NotNull
    private String URI;

    @NotNull
    private String userAgent;

    @NotNull
    private String remoteIpAddress;

    @NotNull
    private LocalDateTime visitedAt;

    public VisitHistoryRecord(String personalLink, String URI, String userAgent, String remoteIpAddress) {
        this.visitedAt = LocalDateTime.now();
        this.personalLink = personalLink;
        this.URI = URI;
        this.userAgent = userAgent;
        this.remoteIpAddress = remoteIpAddress;
    }

    private VisitHistoryRecord() {
    }
}
