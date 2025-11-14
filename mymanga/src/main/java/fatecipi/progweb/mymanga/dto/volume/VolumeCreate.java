package fatecipi.progweb.mymanga.dto.volume;

import fatecipi.progweb.mymanga.models.Manga;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record VolumeCreate(Integer volumeNumber,
                           BigDecimal price,
                           String chapters,
                           LocalDate releaseDate,
                           Integer quantity,
                           Manga manga) {
}
