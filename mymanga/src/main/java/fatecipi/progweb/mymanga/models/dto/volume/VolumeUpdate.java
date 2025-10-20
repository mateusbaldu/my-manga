package fatecipi.progweb.mymanga.models.dto.volume;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record VolumeUpdate(BigDecimal price,
                           String chapters,
                           Integer quantity,
                           LocalDate releaseDate){
}
