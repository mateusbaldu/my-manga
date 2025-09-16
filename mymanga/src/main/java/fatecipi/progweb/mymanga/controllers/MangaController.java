package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.manga.Manga;
import fatecipi.progweb.mymanga.models.manga.MangaCreateAndUpdateDto;
import fatecipi.progweb.mymanga.models.volume.Volume;
import fatecipi.progweb.mymanga.models.volume.VolumeCreateDto;
import fatecipi.progweb.mymanga.models.volume.VolumeResponseDto;
import fatecipi.progweb.mymanga.models.volume.VolumeUpdateDto;
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
    public ResponseEntity<Manga> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.findById(id));
    }

    @GetMapping("/all")
    public List<Manga> listAll() {
        return mangaService.listAll();
    }

    @GetMapping("/best")
    public List<Manga> listByRating() {
        return mangaService.listAllByRating();
    }

    @GetMapping("/releasing")
    public List<Manga> listReleasing() {
        return mangaService.listAllReleasing();
    }

    @GetMapping("/search/{keyword}")
    public List<Manga> listByKeyword(@PathVariable String keyword) {
        return mangaService.findByKeyword(keyword);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Manga> update(@PathVariable Long id, @RequestBody MangaCreateAndUpdateDto mangaDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaDto));
    }

    @PostMapping("/new")
    public ResponseEntity<Manga> create(@RequestBody MangaCreateAndUpdateDto mangaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.save(mangaDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/volumes")
    public ResponseEntity<Volume> addVolumeToManga(@PathVariable Long id, @RequestBody VolumeCreateDto volDto) {
        Volume vol = mangaService.addVolumeToManga(id, volDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vol);
    }

    @GetMapping("/{id}/volumes/all")
    public List<Volume> getAllVolumesForManga(@PathVariable Long id) {
        return mangaService.getAllVolumesForManga(id);
    }

    @PutMapping("/{id}/volumes/{volId}")
    public ResponseEntity<Volume> updateVolume(@PathVariable Long id, @PathVariable Long volId, @RequestBody VolumeUpdateDto volDto) {
        return ResponseEntity.ok(mangaService.updateVolume(id, volId, volDto));
    }

    @DeleteMapping("/{id}/volumes/{volId}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long volId) {
        mangaService.deleteVolume(id, volId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponseDto> getAllVolumesForManga(@PathVariable Long id, @PathVariable Long volId) {
        return ResponseEntity.ok(mangaService.findVolumeById(id, volId));
    }
}
