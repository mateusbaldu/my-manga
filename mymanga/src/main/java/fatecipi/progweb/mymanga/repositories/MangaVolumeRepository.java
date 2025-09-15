package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.MangaVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaVolumeRepository extends JpaRepository<MangaVolume, Long> {
    List<MangaVolume> findByMangaId(Long mangaId);
}
