package fatecipi.progweb.mymanga.models.dto.manga;

import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;

public record MangaCardResponse(
        Long id,
        String title,
        String author,
        String description,
        Double rating,
        MangaStatus status,
        Genres genres,
        String imageUrl
) {
}