package ru.practicum.ewm.service.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.service.categories.model.Category;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.util.DateConstant;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @CreationTimestamp
    @Column(name = "created_on", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "event_date")
    @JsonFormat(pattern = DateConstant.DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "published_on")
    @JsonFormat(pattern = DateConstant.DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "title")
    private String title;
}
