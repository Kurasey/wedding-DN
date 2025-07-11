package io.github.kaurami.wems.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class TimelineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timeline_item_seq")
    @SequenceGenerator(name = "timeline_item_seq", sequenceName = "timeline_item_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private LocalTime eventTime;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String iconName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}