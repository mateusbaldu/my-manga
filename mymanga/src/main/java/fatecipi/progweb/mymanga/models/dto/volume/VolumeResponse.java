package fatecipi.progweb.mymanga.models.dto.volume;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record VolumeResponse(Long id,
                             Integer volumeNumber,
                             BigDecimal price,
                             String chapters,
                             LocalDate releaseDate,
                             Integer quantity,
                             Long mangaId,
                             String mangaTitle) {
}
