package fatecipi.progweb.mymanga.models.manga;

import fatecipi.progweb.mymanga.enums.MangaStatus;

import java.util.List;

public record MangaResponseDto(Long id,
                               String title,
                               String author,
                               Double rating,
                               MangaStatus mangaStatus,
                               List<VolumeResponseDto> volumes) {
}
