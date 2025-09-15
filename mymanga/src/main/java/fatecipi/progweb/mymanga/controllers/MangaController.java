package fatecipi.progweb.mymanga.controllers;

import fatecipi.progweb.mymanga.models.manga.Manga;
import fatecipi.progweb.mymanga.models.manga.MangaCreateDto;
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
    public ResponseEntity<Manga> update(@PathVariable Long id, @RequestBody MangaCreateDto mangaCreateDto) {
        return ResponseEntity.ok(mangaService.update(id, mangaCreateDto));
    }

    @PostMapping("/new")
    public ResponseEntity<Manga> create(@RequestBody Manga manga) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mangaService.save(manga));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mangaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
