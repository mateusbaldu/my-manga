package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.manga.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaVolumeRepository extends JpaRepository<Volume, Long> {
    List<Volume> findByMangaId(Long mangaId);
}
