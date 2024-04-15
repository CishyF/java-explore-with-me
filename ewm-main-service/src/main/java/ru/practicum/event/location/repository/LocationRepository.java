package ru.practicum.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.location.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
