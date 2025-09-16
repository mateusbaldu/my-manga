package fatecipi.progweb.mymanga.models.manga;

import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.models.volume.VolumeResponseDto;

import java.util.List;

public record MangaResponseDto(Long id,
                               String title,
                               String author,
                               String description,
                               Double rating,
                               String keywords,
                               MangaStatus mangaStatus,
                               Genres genres,
                               List<VolumeResponseDto> volumes) {
}
