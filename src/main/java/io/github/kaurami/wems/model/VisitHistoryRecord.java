package io.github.kaurami.wems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class VisitHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Связь с семьей для легкого доступа к имени
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

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

    public VisitHistoryRecord(Family family, String URI, String userAgent, String remoteIpAddress) {
        this.visitedAt = LocalDateTime.now();
        this.family = family;
        this.personalLink = family.getPersonalLink(); // Дублируем для обратной совместимости, если понадобится
        this.URI = URI;
        this.userAgent = userAgent;
        this.remoteIpAddress = remoteIpAddress;
    }

    private VisitHistoryRecord() {
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Family getFamily() { return family; }
    public void setFamily(Family family) { this.family = family; }
    public String getPersonalLink() { return personalLink; }
    public void setPersonalLink(String personalLink) { this.personalLink = personalLink; }
    public String getURI() { return URI; }
    public void setURI(String URI) { this.URI = URI; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getRemoteIpAddress() { return remoteIpAddress; }
    public void setRemoteIpAddress(String remoteIpAddress) { this.remoteIpAddress = remoteIpAddress; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
}