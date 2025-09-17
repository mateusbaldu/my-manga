package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.manga.MangaCreateAndUpdateDto;
import fatecipi.progweb.mymanga.dto.manga.MangaResponseDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeCreateDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponseDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdateDto;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.manga.*;
import fatecipi.progweb.mymanga.models.volume.*;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<MangaResponseDto> listAll() {
        List<Manga> mangas = mangaRepository.findAll();
        return mangas.stream()
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public MangaResponseDto findById(Long id) {
        Manga m = findByIdWithoutDto(id);
        return mangaMapper.toMangaResponseDto(m);
    }

    public Manga findByIdWithoutDto(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + "not found"));
    }

    public List<MangaResponseDto> listAllByRating() {
        //pagination
        return mangaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Manga::getRating).reversed())
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public List<MangaResponseDto> listAllReleasing() {
        return mangaRepository.findAll()
                .stream()
                .filter(manga -> manga.getStatus() == MangaStatus.RELEASING)
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public List<MangaResponseDto> findByKeyword(String keyword) {
        List<Manga> mangas = mangaRepository.findByKeyword(keyword).orElseThrow(() -> new ResourceNotFoundException("No manga with " + keyword + " was found"));
        return mangas.stream()
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        mangaRepository.delete(findByIdWithoutDto(id));
    }

    public MangaResponseDto update(Long id, MangaCreateAndUpdateDto mangaDto) {
        Manga m = findByIdWithoutDto(id);
        mangaMapper.mapManga(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.toMangaResponseDto(m);
    }

    public MangaResponseDto save(MangaCreateAndUpdateDto mangaDto) {
        if (mangaRepository.existsByTitle(mangaDto.title())) {
            throw new ResourceAlreadyExistsException(mangaDto.title() + " já existe.");
        }
        Manga m = new Manga();
        mangaMapper.mapManga(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.toMangaResponseDto(m);
    }



    public VolumeResponseDto addVolumeToManga(Long mangaId, VolumeCreateDto volDto) {
        Manga m = findByIdWithoutDto(mangaId);
        Volume vol = new Volume();
        volumeMapper.mapCreateVolume(volDto, vol);
        vol.setManga(m);
        volumeRepository.save(vol);
        return volumeMapper.toVolumeResponseDto(vol);
    }

    public List<VolumeResponseDto> getAllVolumesForManga(Long mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        List<Volume> volumes = volumeRepository.findByMangaId(mangaId);
        return volumes.stream()
                .map(vol -> volumeMapper.toVolumeResponseDto(vol))
                .collect(Collectors.toList());
    }

    public VolumeResponseDto findVolumeById(Long mangaId, Long volumeId) {
        Manga m = findByIdWithoutDto(mangaId);
        Volume vol = volumeRepository
                .findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + volumeId + " not found"));
        if (!m.getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        return volumeMapper.toVolumeResponseDto(vol);
    }

    public Volume findByIdNoDto(Long id) {
        return volumeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + id + " not found"));
    }

    public VolumeResponseDto updateVolume(Long mangaId, Long volumeId, VolumeUpdateDto dto) {
        Volume vol = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with ID " + volumeId + " not found."));
        if (!vol.getManga().getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        volumeMapper.mapUpdateVolume(dto, vol);
        volumeRepository.save(vol);

        return volumeMapper.toVolumeResponseDto(vol);
    }

    public void deleteVolume(Long mangaId, Long volumeId) {
        Manga m = findByIdWithoutDto(mangaId);
        if (!volumeRepository.existsById(volumeId)) {
            throw new ResourceNotFoundException("Volume com ID " + volumeId + " não encontrado.");
        }
        if (!m.getId().equals(mangaId)) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        volumeRepository.delete(findByIdNoDto(volumeId));
    }
}
