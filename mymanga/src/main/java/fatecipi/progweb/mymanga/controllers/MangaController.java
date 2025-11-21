package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.dto.manga.MangaCardResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaUpdate;
import fatecipi.progweb.mymanga.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.services.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mangás", description = "Endpoints for mangá and volume management")
@RestController
@RequestMapping("/mangas")
@RequiredArgsConstructor
public class MangaController {
    private final MangaService mangaService;

    @Operation(summary = "Search a mangá by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mangá found successfully"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> getMangaById(@PathVariable Long id) {
        return ResponseEntity.ok(mangaService.getMangaResponseById(id));
    }

    @Operation(summary = "List all available mangás")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mangás found successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<MangaCardResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(mangaService.listAll(pageable));
    }

    @Operation(summary = "Search a mangá by keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mangás found successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<MangaResponse>> findByKeyword(Pageable pageable, @RequestParam String keyword) {
        return ResponseEntity.ok(mangaService.findByKeyword(keyword, pageable));
    }

    @Operation(summary = "Update a mangá from a mangá id and a mangá update body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mangá updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> update(@PathVariable Long id, @Valid @RequestBody MangaUpdate mangaDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaDto));
    }

    @Operation(summary = "Create a new mangá from a mangá creation body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mangá created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PostMapping("/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MangaResponse> create(@Valid @RequestBody MangaCreate mangaDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.create(mangaDto));
    }

    @Operation(summary = "Delete a mangá by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mangá deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        mangaService.deleteMangaById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search a volume by mangá id and volume id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volume found successfully"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found / Volume with id not found")
    })
    @GetMapping("/{id}/volumes/{volId}")
    public ResponseEntity<VolumeResponse> findVolumeById(@PathVariable Long id, @PathVariable Long volId) {
        return ResponseEntity.ok(mangaService.getVolumeResponseById(id, volId));
    }

    @Operation(summary = "List all volumes for a specific mangá")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volumes found successfully"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found")
    })
    @GetMapping("/{id}/volumes/all")
    public ResponseEntity<Page<VolumeResponse>> getAllVolumesForManga(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(mangaService.getAllVolumesForManga(id, pageable));
    }

    @Operation(summary = "Add volumes to a mangá from a mangá id and a list of volume creation bodies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Volumes created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PostMapping("/{id}/volumes/new")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<VolumeResponse>> addVolumesToManga(@PathVariable Long id, @Valid @RequestBody List<VolumeCreate> volDto) {
        List<VolumeResponse> vol = mangaService.addVolumesToManga(id, volDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vol);
    }

    @Operation(summary = "Update a volume from a mangá id, volume id and a volume update body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volume updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found / Volume with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PatchMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<VolumeResponse> updateVolume(@PathVariable Long id, @PathVariable Long volId, @Valid @RequestBody VolumeUpdate volDto) {
        return ResponseEntity.ok(mangaService.updateVolume(id, volId, volDto));
    }

    @Operation(summary = "Delete a volume by mangá id and volume id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Volume deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mangá with id not found / Volume with id not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @DeleteMapping("/{id}/volumes/{volId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteVolume(@PathVariable Long id, @PathVariable Long volId) {
        mangaService.deleteVolumeById(id, volId);
        return ResponseEntity.noContent().build();
    }
}
