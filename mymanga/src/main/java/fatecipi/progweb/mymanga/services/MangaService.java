package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.configs.mappers.MangaMapper;
import fatecipi.progweb.mymanga.configs.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Volume;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MangaMapper mangaMapper;
    private final VolumeRepository volumeRepository;
    private final VolumeMapper volumeMapper;

    public MangaService(MangaRepository mangaRepository, MangaMapper mangaMapper, VolumeRepository volumeRepository, VolumeMapper volumeMapper) {
        this.mangaRepository = mangaRepository;
        this.mangaMapper = mangaMapper;
        this.volumeRepository = volumeRepository;
        this.volumeMapper = volumeMapper;
    }

    public Page<MangaResponse> listAll(Pageable pageable)  {
        return mangaRepository.findAll(pageable).map(manga -> mangaMapper.toMangaResponseDto(manga));
    }

    public MangaResponse findById(Long id) {
        Manga m = findByIdWithoutDto(id);
        return mangaMapper.toMangaResponseDto(m);
    }

    public Manga findByIdWithoutDto(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
    }

    public Page<MangaResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Manga> mangaPage = mangaRepository.findByKeyword(keyword, pageable);
        return mangaPage.map(manga -> mangaMapper.toMangaResponseDto(manga));
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



    public List<VolumeResponse> addVolumesToManga(Long mangaId, List<VolumeCreate> volDto) {
        Manga m = findByIdWithoutDto(mangaId);
        return volDto.stream()
                .map(vol -> {
                    Volume volume = new Volume();
                    volumeMapper.mapCreateVolume(vol, volume);
                    volume.setManga(m);
                    Volume savedVolume = volumeRepository.save(volume);

                    return volumeMapper.toVolumeResponseDto(savedVolume);
                })
                .toList();
    }

    public Page<VolumeResponse> getAllVolumesForManga(Long mangaId, Pageable pageable) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        Page<Volume> volumePage = volumeRepository.findByMangaId(mangaId, pageable);
        return volumePage.map(vol -> volumeMapper.toVolumeResponseDto(vol));
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
