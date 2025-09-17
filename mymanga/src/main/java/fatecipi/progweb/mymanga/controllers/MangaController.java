package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.manga.MangaCreateAndUpdateDto;
import fatecipi.progweb.mymanga.dto.manga.MangaResponseDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeCreateDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponseDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdateDto;
import fatecipi.progweb.mymanga.services.MangaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-manga/mangas")
public class MangaController {
    @Autowired
    private MangaService mangaService;

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.findById(id));
    }

    @GetMapping("/all")
    public List<MangaResponseDto> listAll() {
        return mangaService.listAll();
    }

    @GetMapping("/best")
    public List<MangaResponseDto> listByRating() {
        return mangaService.listAllByRating();
    }

    @GetMapping("/releasing")
    public List<MangaResponseDto> listReleasing() {
        return mangaService.listAllReleasing();
    }

    @GetMapping("/search/{keyword}")
    public List<MangaResponseDto> listByKeyword(@PathVariable String keyword) {
        return mangaService.findByKeyword(keyword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MangaResponseDto> update(@PathVariable Long id, @RequestBody MangaCreateAndUpdateDto mangaDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaDto));
    }

    @PostMapping("/new")
    public ResponseEntity<MangaResponseDto> create(@RequestBody MangaCreateAndUpdateDto mangaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.save(mangaDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/volumes/new")
    public ResponseEntity<VolumeResponseDto> addVolumeToManga(@PathVariable Long id, @RequestBody VolumeCreateDto volDto) {
        VolumeResponseDto vol = mangaService.addVolumeToManga(id, volDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vol);
    }

    @GetMapping("/{id}/volumes/all")
    public List<VolumeResponseDto> getAllVolumesForManga(@PathVariable Long id) {
        return mangaService.getAllVolumesForManga(id);
    }

    @PutMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponseDto> updateVolume(@PathVariable Long id, @PathVariable Long volId, @RequestBody VolumeUpdateDto volDto) {
        return ResponseEntity.ok(mangaService.updateVolume(id, volId, volDto));
    }

    @DeleteMapping("/{id}/volumes/{volId}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long volId) {
        mangaService.deleteVolume(id, volId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponseDto> findVolumeById(@PathVariable Long id, @PathVariable Long volId) {
        return ResponseEntity.ok(mangaService.findVolumeById(id, volId));
    }
}
