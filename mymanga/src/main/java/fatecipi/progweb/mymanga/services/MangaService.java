package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.MangaVolume;
import fatecipi.progweb.mymanga.models.dtos.MangaDto;
import fatecipi.progweb.mymanga.models.mappers.MangaMapper;
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

    public Manga update(Long id, MangaDto mangaDto) {
        Manga mangaSearched = findById(id);
        mangaMapper.mapManga(mangaDto, mangaSearched);
        return mangaRepository.save(mangaSearched);
    }

    public Manga save(Manga manga) {
        Manga obj = mangaRepository
                .findById(manga.getId())
                .orElseThrow(() -> new ResourceAlreadyExistsException(manga.getTitle() + " already exists"));

        return mangaRepository.save(obj);
    }

    @Transactional
    public MangaVolume addVolumeToManga(Long mangaId, MangaVolume volume) {
        volume.setManga(findById(mangaId));
        return mangaVolumeRepository.save(volume);
    }

    @Transactional(readOnly = true)
    public List<MangaVolume> getAllVolumesForManga(Long mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        return mangaVolumeRepository.findByMangaId(mangaId);
    }
}
