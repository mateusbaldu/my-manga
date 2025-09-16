package fatecipi.progweb.mymanga.models.manga;

import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;

public record MangaCreateAndUpdateDto(String title,
                                      String author,
                                      String description,
                                      Double rating,
                                      MangaStatus status,
                                      Genres genres,
                                      String keywords) {
}
