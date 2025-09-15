package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {
    @Query(value = "SELECT * FROM manga WHERE name ILIKE ?1 OR description ILIKE ?1 OR keywords ILIKE ?1",
            nativeQuery = true)
    Optional<List<Manga>> findByKeyword(String keyword);
}
