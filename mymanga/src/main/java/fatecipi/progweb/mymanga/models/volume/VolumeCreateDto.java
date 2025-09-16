package fatecipi.progweb.mymanga.models.volume;

import fatecipi.progweb.mymanga.models.manga.Manga;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VolumeCreateDto(Integer volumeNumber,
                              BigDecimal price,
                              String chapters,
                              LocalDate releaseDate,
                              Integer quantity,
                              Manga manga) {
}
