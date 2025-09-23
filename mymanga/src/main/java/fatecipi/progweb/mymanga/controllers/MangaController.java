package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.Users;
import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.services.MangaService;
import fatecipi.progweb.mymanga.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my-manga/mangas")
public class MangaController {
    @Autowired
    private MangaService mangaService;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.findById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<MangaResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(mangaService.listAll(pageable));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<MangaResponse>> listByKeyword(Pageable pageable, @PathVariable String keyword) {
        return ResponseEntity.ok(mangaService.findByKeyword(keyword, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> update(@PathVariable Long id, @RequestBody MangaCreateAndUpdate mangaDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaDto));
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> create(@RequestBody MangaCreateAndUpdate mangaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.save(mangaDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/volumes/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<VolumeResponse>> addVolumesToManga(@PathVariable Long id, @RequestBody List<VolumeCreate> volDto) {
        List<VolumeResponse> vol = mangaService.addVolumesToManga(id, volDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vol);
    }

    @GetMapping("/{id}/volumes/all")
    public ResponseEntity<Page<VolumeResponse>> getAllVolumesForManga(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(mangaService.getAllVolumesForManga(id, pageable));
    }

    @PutMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<VolumeResponse> updateVolume(@PathVariable Long id, @PathVariable Long volId, @RequestBody VolumeUpdate volDto) {
        return ResponseEntity.ok(mangaService.updateVolume(id, volId, volDto));
    }

    @DeleteMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteVolume(@PathVariable Long id, @PathVariable Long volId) {
        mangaService.deleteVolume(id, volId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponse> findVolumeById(@PathVariable Long id, @PathVariable Long volId) {
        return ResponseEntity.ok(mangaService.findVolumeById(id, volId));
    }
}
