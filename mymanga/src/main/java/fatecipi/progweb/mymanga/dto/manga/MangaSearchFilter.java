package fatecipi.progweb.mymanga.dto.manga;

import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;

public record MangaSearchFilter(
        String title,
        String author,
        Double rating,
        String keywords,
        MangaStatus status,
        Genres genres
) {
}
