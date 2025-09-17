package fatecipi.progweb.mymanga.dto.manga;

import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponseDto;

import java.util.Set;

public record MangaResponseDto(Long id,
                               String title,
                               String author,
                               String description,
                               Double rating,
                               String keywords,
                               MangaStatus status,
                               Genres genres,
                               Set<VolumeResponseDto> volumes) {
}
