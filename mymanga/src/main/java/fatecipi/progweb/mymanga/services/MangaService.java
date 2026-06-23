package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.dto.manga.*;
import fatecipi.progweb.mymanga.mappers.MangaMapper;
import fatecipi.progweb.mymanga.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Volume;
import fatecipi.progweb.mymanga.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import fatecipi.progweb.mymanga.specifications.MangaSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MangaMapper mangaMapper;
    private final VolumeRepository volumeRepository;
    private final VolumeMapper volumeMapper;

    @Transactional(readOnly = true)
    public Page<MangaCardResponse> listAll(Pageable pageable, MangaSearchFilter filter) {
        return mangaRepository.findAll(MangaSpec.filter(filter), pageable).map(mangaMapper::toMangaCardResponse);
    }

    @Cacheable(value = "mangaCache", key = "#id")
    @Transactional(readOnly = true)
    public MangaResponse getMangaResponseById(Long id) {
        Manga m = mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
        return mangaMapper.responseMapping(m);
    }

    public Manga findMangaById(Long id) {
        return mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public Page<MangaResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Manga> mangaPage = mangaRepository.findByKeyword(keyword, pageable);
        return mangaPage.map(mangaMapper::responseMapping);
    }

    @CacheEvict(value = "mangaCache", key = "#id")
    public void deleteMangaById(Long id) {
        if (!mangaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Manga with id " + id + " dont exists");
        }
        mangaRepository.deleteById(id);
    }

    @CacheEvict(value = "mangaCache", key = "#id")
    @Transactional
    public MangaResponse update(Long id, MangaUpdate mangaDto) {
        Manga m = mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
        mangaMapper.updateMapping(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.responseMapping(m);
    }

    @Transactional
    public MangaResponse create(MangaCreate mangaDto) {
        if (mangaRepository.existsByTitle(mangaDto.title())) {
            throw new ResourceAlreadyExistsException(mangaDto.title() + " já existe.");
        }
        Manga m = new Manga();
        mangaMapper.createMapping(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.responseMapping(m);
    }

    @Transactional
    public List<VolumeResponse> addVolumesToManga(Long mangaId, List<VolumeCreate> volDto) {
        if (volDto.isEmpty())
            throw new IllegalArgumentException("The list of volumes cannot be empty.");
        Manga m = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new ResourceNotFoundException("Manga with id " + mangaId + " was not found"));

        return volDto.stream()
                .map(vol -> {
                    Volume volume = new Volume();
                    volumeMapper.createMapping(vol, volume);
                    volume.setManga(m);
                    Volume savedVolume = volumeRepository.save(volume);

                    return volumeMapper.responseMapping(savedVolume);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<VolumeResponse> getAllVolumesForManga(Long mangaId, Pageable pageable) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        Page<Volume> volumePage = volumeRepository.findByMangaId(mangaId, pageable);
        return volumePage.map(volumeMapper::responseMapping);
    }

    @Transactional(readOnly = true)
    public VolumeResponse getVolumeResponseById(Long mangaId, Long volumeId) {
        Volume vol = getVolumeAssociatedWithManga(mangaId, volumeId);
        return volumeMapper.responseMapping(vol);
    }

    public Volume getVolumeById(Long id) {
        return volumeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + id + " not found"));
    }

    @Transactional
    public VolumeResponse updateVolume(Long mangaId, Long volumeId, VolumeUpdate dto) {
        Volume v = getVolumeAssociatedWithManga(mangaId, volumeId);
        volumeMapper.updateMapping(dto, v);
        volumeRepository.save(v);

        return volumeMapper.responseMapping(v);
    }

    @Transactional
    public void deleteVolumeById(Long mangaId, Long volumeId) {
        Volume v = getVolumeAssociatedWithManga(mangaId, volumeId);
        volumeRepository.deleteById(v.getId());
    }

    public Volume getVolumeAssociatedWithManga(Long mangaId, Long volumeId) {
        Volume v = getVolumeById(volumeId);
        if (!mangaId.equals(v.getManga().getId())) {
            throw new IllegalArgumentException(
                    "Volume " + volumeId + " isn't associated with manga with id " + mangaId);
        }
        return v;
    }
}
