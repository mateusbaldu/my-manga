package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.dto.manga.MangaCardResponse;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.services.MangaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-manga/mangas")
@RequiredArgsConstructor
public class MangaController {
    private final MangaService mangaService;

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> getMangaById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.getMangaResponseById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<MangaCardResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(mangaService.listAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MangaResponse>> findByKeyword(Pageable pageable, @RequestParam String keyword) {
        return ResponseEntity.ok(mangaService.findByKeyword(keyword, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> update(@PathVariable Long id, @Valid @RequestBody MangaUpdate mangaDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaDto));
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> create(@Valid @RequestBody MangaCreate mangaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.create(mangaDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        mangaService.deleteMangaById(id);
        return ResponseEntity.noContent().build();
    }





    @GetMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponse> findVolumeById(@PathVariable Long id, @PathVariable Long volId) {
        return ResponseEntity.ok(mangaService.getVolumeResponseById(id, volId));
    }

    @GetMapping("/{id}/volumes/all")
    public ResponseEntity<Page<VolumeResponse>> getAllVolumesForManga(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(mangaService.getAllVolumesForManga(id, pageable));
    }

    @PostMapping("/{id}/volumes/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<VolumeResponse>> addVolumesToManga(@PathVariable Long id, @Valid @RequestBody List<VolumeCreate> volDto) {
        List<VolumeResponse> vol = mangaService.addVolumesToManga(id, volDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vol);
    }

    @PatchMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<VolumeResponse> updateVolume(@PathVariable Long id, @PathVariable Long volId, @Valid @RequestBody VolumeUpdate volDto) {
        return ResponseEntity.ok(mangaService.updateVolume(id, volId, volDto));
    }

    @DeleteMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteVolume(@PathVariable Long id, @PathVariable Long volId) {
        mangaService.deleteVolumeById(id, volId);
        return ResponseEntity.noContent().build();
    }
}
