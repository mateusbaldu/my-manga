package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Manga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {
    @Query("SELECT m FROM Manga m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Manga> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Override
    @Query(value = "SELECT m FROM Manga m",
            countQuery = "SELECT COUNT(m) FROM Manga m")
    Page<Manga> findAll(Pageable pageable);

    boolean existsByTitle(String title);
}