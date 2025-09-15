package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.manga.Manga;
import fatecipi.progweb.mymanga.models.manga.MangaCreateDto;
import fatecipi.progweb.mymanga.models.manga.Volume;
import fatecipi.progweb.mymanga.models.manga.MangaMapper;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.MangaVolumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MangaService {
    @Autowired
    private MangaRepository mangaRepository;
    @Autowired
    private MangaMapper mangaMapper;
    @Autowired
    private MangaVolumeRepository mangaVolumeRepository;

    public List<Manga> listAll() {
        //pagination
        return mangaRepository.findAll();
    }

    public Manga findById(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + "not found"));
    }

    public List<Manga> listAllByRating() {
        //pagination
        return mangaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Manga::getRating).reversed())
                .collect(Collectors.toList());
    }

    public List<Manga> listAllReleasing() {
        //pagination
        return mangaRepository.findAll()
                .stream()
                .filter(manga -> manga.getStatus() == MangaStatus.RELEASING)
                .collect(Collectors.toList());
    }

    public List<Manga> findByKeyword(String keyword) {
        //pagination
        return mangaRepository.findByKeyword(keyword).orElseThrow(() -> new ResourceNotFoundException("No manga with " + keyword + " was found"));
    }

    public void deleteById(Long id) {
        mangaRepository.delete(findById(id));
    }

    public Manga update(Long id, MangaCreateDto mangaCreateDto) {
        Manga mangaSearched = findById(id);
        mangaMapper.mapManga(mangaCreateDto, mangaSearched);
        return mangaRepository.save(mangaSearched);
    }

    public Manga save(Manga manga) {
        if (mangaRepository.existsByTitle(manga.getTitle())) {
            throw new ResourceAlreadyExistsException(manga.getTitle() + " já existe.");
        }
        return mangaRepository.save(manga);
    }

    @Transactional
    public Volume addVolumeToManga(Long mangaId, Volume volume) {
        volume.setManga(findById(mangaId));
        return mangaVolumeRepository.save(volume);
    }

    @Transactional(readOnly = true)
    public List<Volume> getAllVolumesForManga(Long mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        return mangaVolumeRepository.findByMangaId(mangaId);
    }
}
