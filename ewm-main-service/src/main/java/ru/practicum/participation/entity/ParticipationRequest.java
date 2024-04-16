package ru.practicum.participation.entity;

import lombok.*;
import ru.practicum.event.entity.Event;
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
@Table(name = "participation_request")
public class ParticipationRequest {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @Column(name = "created")
    @EqualsAndHashCode.Exclude
    private LocalDateTime created;

    @Column(name = "status")
    @EqualsAndHashCode.Exclude
    @Enumerated(EnumType.ORDINAL)
    private ParticipationRequestStatus status;
}
