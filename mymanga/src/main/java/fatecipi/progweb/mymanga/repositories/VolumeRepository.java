package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {
    List<Volume> findByMangaId(Long mangaId);
}
