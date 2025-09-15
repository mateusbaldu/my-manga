package fatecipi.progweb.mymanga.models.manga;

import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;

public record MangaUpdateDto(String title,
                             String author,
                             String description,
                             Double rating,
                             String keywords,
                             MangaStatus mangaStatus,
                             Genres genres) {
}
