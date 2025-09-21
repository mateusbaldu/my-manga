package fatecipi.progweb.mymanga.models.dto.volume;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VolumeUpdate(BigDecimal price,
                           String chapters,
                           Integer quantity,
                           LocalDate releaseDate){
}
