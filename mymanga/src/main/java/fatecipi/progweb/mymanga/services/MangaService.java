package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.MangaMapper;
import fatecipi.progweb.mymanga.configs.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Volume;
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

    public List<MangaResponse> listAll() {
        return mangaRepository.findAll().stream()
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public MangaResponse findById(Long id) {
        Manga m = findByIdWithoutDto(id);
        return mangaMapper.toMangaResponseDto(m);
    }

    public Manga findByIdWithoutDto(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + "not found"));
    }

    public List<MangaResponse> listAllByRating() {
        //pagination
        return mangaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Manga::getRating).reversed())
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public List<MangaResponse> listAllReleasing() {
        return mangaRepository.findAll()
                .stream()
                .filter(manga -> manga.getStatus() == MangaStatus.RELEASING)
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public List<MangaResponse> findByKeyword(String keyword) {
        List<Manga> mangas = mangaRepository.findByKeyword(keyword).orElseThrow(() -> new ResourceNotFoundException("No manga with " + keyword + " was found"));
        return mangas.stream()
                .map(manga -> mangaMapper.toMangaResponseDto(manga))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        mangaRepository.delete(findByIdWithoutDto(id));
    }

    public MangaResponse update(Long id, MangaCreateAndUpdate mangaDto) {
        Manga m = findByIdWithoutDto(id);
        mangaMapper.mapManga(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.toMangaResponseDto(m);
    }

    public MangaResponse save(MangaCreateAndUpdate mangaDto) {
        if (mangaRepository.existsByTitle(mangaDto.title())) {
            throw new ResourceAlreadyExistsException(mangaDto.title() + " já existe.");
        }
        Manga m = new Manga();
        mangaMapper.mapManga(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.toMangaResponseDto(m);
    }



    public VolumeResponse addVolumeToManga(Long mangaId, VolumeCreate volDto) {
        Manga m = findByIdWithoutDto(mangaId);
        Volume vol = new Volume();
        volumeMapper.mapCreateVolume(volDto, vol);
        vol.setManga(m);
        volumeRepository.save(vol);
        return volumeMapper.toVolumeResponseDto(vol);
    }

    public List<VolumeResponse> getAllVolumesForManga(Long mangaId) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        List<Volume> volumes = volumeRepository.findByMangaId(mangaId);
        return volumes.stream()
                .map(vol -> volumeMapper.toVolumeResponseDto(vol))
                .collect(Collectors.toList());
    }

    public VolumeResponse findVolumeById(Long mangaId, Long volumeId) {
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

    public VolumeResponse updateVolume(Long mangaId, Long volumeId, VolumeUpdate dto) {
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
