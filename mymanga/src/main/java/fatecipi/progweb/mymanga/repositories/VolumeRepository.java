package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Volume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {
    Page<Volume> findByMangaId(Long mangaId, Pageable pageable);
}
