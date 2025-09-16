package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.manga.*;
import fatecipi.progweb.mymanga.models.volume.*;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
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
    private VolumeRepository volumeRepository;
    @Autowired
    private VolumeMapper volumeMapper;

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

    public Manga update(Long id, MangaCreateAndUpdateDto mangaDto) {
        Manga m = findById(id);
        mangaMapper.mapManga(mangaDto, m);
        return mangaRepository.save(m);
    }

    public Manga save(MangaCreateAndUpdateDto mangaDto) {
        if (mangaRepository.existsByTitle(mangaDto.title())) {
            throw new ResourceAlreadyExistsException(mangaDto.title() + " já existe.");
        }
        Manga m = new Manga();
        mangaMapper.mapManga(mangaDto, m);
        return mangaRepository.save(m);
    }

    public Volume addVolumeToManga(Long mangaId, VolumeCreateDto volDto) {
        Manga m = findById(mangaId);
        Volume v = new Volume();
        volumeMapper.mapCreateVolume(volDto, v);
        v.setManga(m);
        return volumeRepository.save(v);
    }

    public List<Volume> getAllVolumesForManga(Long mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        return volumeRepository.findByMangaId(mangaId);
    }

    public VolumeResponseDto findVolumeById(Long mangaId, Long volumeId) {
        Manga m = findById(mangaId);
        Volume vol = volumeRepository
                .findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + volumeId + " not found"));
        if (!m.getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        return VolumeResponseDto.builder().
                id(vol.getId()).
                volumeNumber(vol.getVolumeNumber()).
                price(vol.getPrice()).
                chapters(vol.getChapters()).
                releaseDate(vol.getReleaseDate()).
                mangaId(vol.getManga().getId()).
                mangaTitle(vol.getManga().getTitle()).
                build();
    }

    public Volume findByIdNoDto(Long id) {
        return volumeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + id + " not found"));
    }

    public Volume updateVolume(Long mangaId, Long volumeId, VolumeUpdateDto dto) {
        Volume vol = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with ID " + volumeId + " not found."));
        if (!vol.getManga().getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        volumeMapper.mapUpdateVolume(dto, vol);
        return volumeRepository.save(vol);
    }

    public void deleteVolume(Long mangaId, Long volumeId) {
        Manga m = findById(mangaId);
        if (!volumeRepository.existsById(volumeId)) {
            throw new ResourceNotFoundException("Volume com ID " + volumeId + " não encontrado.");
        }
        if (!m.getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        volumeRepository.delete(findByIdNoDto(volumeId));
    }
}
