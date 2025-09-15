package fatecipi.progweb.mymanga.models.dtos;

import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.models.MangaVolume;

import java.util.Set;

public record MangaDto(String title,
                       String author,
                       String description,
                       Double rating,
                       MangaStatus status,
                       Genres genres,
                       String keywords,
                       Set<MangaVolume> volumes) {
}
