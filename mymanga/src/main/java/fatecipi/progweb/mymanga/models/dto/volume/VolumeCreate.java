package fatecipi.progweb.mymanga.models.dto.volume;

import fatecipi.progweb.mymanga.models.Manga;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VolumeCreate(Integer volumeNumber,
                           BigDecimal price,
                           String chapters,
                           LocalDate releaseDate,
                           Integer quantity,
                           Manga manga) {
}
