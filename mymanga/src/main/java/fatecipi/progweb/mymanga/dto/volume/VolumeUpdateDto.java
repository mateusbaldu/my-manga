package fatecipi.progweb.mymanga.dto.volume;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VolumeUpdateDto (BigDecimal price,
                               String chapters,
                               Integer quantity,
                               LocalDate releaseDate){
}
