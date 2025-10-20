package fatecipi.progweb.mymanga.models.dto.manga;

import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MangaUpdate(@NotNull(message = "Field can't be null")
                          String title,
                          String author,
                          String description,
                          Double rating,
                          MangaStatus status,
                          Genres genres,
                          String keywords) {
}
