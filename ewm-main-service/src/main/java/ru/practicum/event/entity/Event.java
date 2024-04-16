package ru.practicum.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.category.entity.Category;
import ru.practicum.event.location.entity.Location;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    @EqualsAndHashCode.Exclude
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_on")
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdOn;

    @Column(name = "description")
    @EqualsAndHashCode.Exclude
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "participant_limit")
    @EqualsAndHashCode.Exclude
    private int participantLimit;

    @Column(name = "published_on")
    @EqualsAndHashCode.Exclude
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    @EqualsAndHashCode.Exclude
    private boolean requestModeration;

    @Column(name = "state")
    @EqualsAndHashCode.Exclude
    @Enumerated(EnumType.ORDINAL)
    private EventState state;

    @JsonIgnore
    public boolean isModerated() {
        return requestModeration || participantLimit != 0;
    }

    @JsonIgnore
    public boolean isNotModerated() {
        return !isModerated();
    }
}
