package fatecipi.progweb.mymanga.models.manga;

import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VolumeUpdateDto (BigDecimal price,
                               Integer quantity,
                               LocalDate releaseDate){
}
