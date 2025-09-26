package ru.practicum.ewm.service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.service.event.model.Request;
import ru.practicum.ewm.service.event.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {

    Request findByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    Long countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByRequester_Id(Long requesterId);

    List<Request> findByEvent_Id(Long eventId);

    List<Request> findByStatus(RequestStatus status);

}
