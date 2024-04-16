package ru.practicum.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.entity.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = "SELECT c FROM Compilation c WHERE (:pinned IS NULL OR :pinned = c.pinned)")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
