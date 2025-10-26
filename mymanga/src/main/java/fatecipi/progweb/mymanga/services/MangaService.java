package fatecipi.progweb.mymanga.services;

import fatecipi.progweb.mymanga.mappers.MangaMapper;
import fatecipi.progweb.mymanga.mappers.VolumeMapper;
import fatecipi.progweb.mymanga.exceptions.ResourceAlreadyExistsException;
import fatecipi.progweb.mymanga.exceptions.ResourceNotFoundException;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.Volume;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.repositories.MangaRepository;
import fatecipi.progweb.mymanga.repositories.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final MangaMapper mangaMapper;
    private final VolumeRepository volumeRepository;
    private final VolumeMapper volumeMapper;


    public Page<MangaResponse> listAll(Pageable pageable)  {
        return mangaRepository.findAll(pageable).map(mangaMapper::responseMapping);
    }

    public MangaResponse getMangaResponseById(Long id) {
        Manga m = findMangaById(id);
        return mangaMapper.responseMapping(m);
    }

    public Manga findMangaById(Long id) {
        return mangaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manga with id " + id + " was not found"));
    }

    public Page<MangaResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Manga> mangaPage = mangaRepository.findByKeyword(keyword, pageable);
        return mangaPage.map(mangaMapper::responseMapping);
    }

    public void deleteMangaById(Long id) {
        if(!mangaRepository.existsById(id)) {
            throw new IllegalArgumentException("Manga with id " + id + " dont exists");
        }
        mangaRepository.deleteById(id);
    }

    public MangaResponse update(Long id, MangaUpdate mangaDto) {
        Manga m = findMangaById(id);
        mangaMapper.updateMapping(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.responseMapping(m);
    }

    public MangaResponse create(MangaCreate mangaDto) {
        if (mangaRepository.existsByTitle(mangaDto.title())) {
            throw new ResourceAlreadyExistsException(mangaDto.title() + " já existe.");
        }
        Manga m = new Manga();
        mangaMapper.createMapping(mangaDto, m);
        mangaRepository.save(m);
        return mangaMapper.responseMapping(m);
    }



    public List<VolumeResponse> addVolumesToManga(Long mangaId, List<VolumeCreate> volDto) {
        if (volDto.isEmpty()) throw new IllegalArgumentException("The list of volumes cannot be empty.");
        Manga m = findMangaById(mangaId);

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

    public Page<VolumeResponse> getAllVolumesForManga(Long mangaId, Pageable pageable) {
        if (!mangaRepository.existsById(mangaId)) {
            throw new ResourceNotFoundException("Manga with id " + mangaId + " not found");
        }
        Page<Volume> volumePage = volumeRepository.findByMangaId(mangaId, pageable);
        return volumePage.map(volumeMapper::responseMapping);
    }

    public VolumeResponse getVolumeResponseById(Long mangaId, Long volumeId) {
        Volume vol = getVolumeAssociatedWithManga(mangaId, volumeId);
        return volumeMapper.responseMapping(vol);
    }

    public Volume getVolumeById(Long id) {
        return volumeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volume with id " + id + " not found"));
    }

    public VolumeResponse updateVolume(Long mangaId, Long volumeId, VolumeUpdate dto) {
        Volume v = getVolumeAssociatedWithManga(mangaId, volumeId);
        volumeMapper.updateMapping(dto, v);
        volumeRepository.save(v);

        return volumeMapper.responseMapping(v);
    }

    public void deleteVolumeById(Long mangaId, Long volumeId) {
        Volume v = getVolumeAssociatedWithManga(mangaId, volumeId);
        volumeRepository.deleteById(v.getId());
    }

    public Volume getVolumeAssociatedWithManga(Long mangaId, Long volumeId) {
        Volume v = getVolumeById(volumeId);
        if (!mangaId.equals(v.getManga().getId())) {
            throw new IllegalArgumentException("Volume " + volumeId + " isn't associated with manga with id " + mangaId);
        }
        return v;
    }
}
