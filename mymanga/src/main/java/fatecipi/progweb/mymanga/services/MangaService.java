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
        return mangaRepository.findAll(pageable).map(mangaMapper::toMangaResponseDto);
    }

    public MangaResponse findById(Long id) {
        Manga m = findMangaByIdWithoutDto(id);
        return mangaMapper.toMangaResponseDto(m);
    }

    public Manga findMangaByIdWithoutDto(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
    }

    public Page<MangaResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Manga> mangaPage = mangaRepository.findByKeyword(keyword, pageable);
        return mangaPage.map(mangaMapper::toMangaResponseDto);
    }

    public void deleteMangaById(Long id) {
        if(!mangaRepository.existsById(id)) {
            throw new IllegalArgumentException("Manga with id " + id + " dont exists");
        }
        mangaRepository.deleteById(id);
    }

    public MangaResponse update(Long id, MangaCreateAndUpdate mangaDto) {
        Manga m = findMangaByIdWithoutDto(id);
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
        if (volDto.isEmpty()) throw new IllegalArgumentException("The list of volumes cannot be empty.");
        Manga m = findMangaByIdWithoutDto(mangaId);

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
        return volumePage.map(volumeMapper::toVolumeResponseDto);
    }

    public VolumeResponse findVolumeById(Long mangaId, Long volumeId) {
        Manga m = findMangaByIdWithoutDto(mangaId);
        Volume vol = volumeRepository
                .findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + volumeId + " not found"));
        if (!vol.getManga().getId().equals(m.getId())) {
            throw new IllegalArgumentException("O volume " + volumeId + " não pertence ao mangá " + mangaId);
        }
        return volumeMapper.toVolumeResponseDto(vol);
    }

    public Volume findVolumeByIdWithoutDto(Long id) {
        return volumeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + id + " not found"));
    }

    public VolumeResponse updateVolume(Long mangaId, Long volumeId, VolumeUpdate dto) {
        Volume v = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with ID " + volumeId + " don't exists"));
        if (!v.getManga().getId().equals(mangaId)) {
            throw new IllegalArgumentException("The volume " + volumeId + " isn't associated with the manga with id " + mangaId);
        }
        volumeMapper.mapUpdateVolume(dto, v);
        volumeRepository.save(v);

        return volumeMapper.toVolumeResponseDto(v);
    }

    public void deleteVolumeById(Long mangaId, Long volumeId) {
        Volume v = findVolumeByIdWithoutDto(volumeId);
        if (!v.getManga().getId().equals(mangaId)) {
            throw new IllegalArgumentException("The volume " + volumeId + " isn't associated with the manga with id " + mangaId);
        }
        volumeRepository.deleteById(volumeId);
    }
}
