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
                             Long mangaId,
                             String mangaTitle) {
    //TODO - adicionar quntidade de volumes disponiveis na resposta
}
