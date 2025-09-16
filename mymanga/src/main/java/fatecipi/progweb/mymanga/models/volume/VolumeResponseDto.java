package fatecipi.progweb.mymanga.models.volume;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record VolumeResponseDto(Long id,
                                Integer volumeNumber,
                                BigDecimal price,
                                String chapters,
                                LocalDate releaseDate,
                                Long mangaId,
                                String mangaTitle) {
}
