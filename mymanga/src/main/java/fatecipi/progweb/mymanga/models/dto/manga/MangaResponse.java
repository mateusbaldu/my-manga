package fatecipi.progweb.mymanga.models.dto.manga;

import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.enums.Genres;
import fatecipi.progweb.mymanga.enums.MangaStatus;

import java.util.Set;

public record MangaResponse(Long id,
                            String title,
                            String author,
                            String description,
                            Double rating,
                            String keywords,
                            MangaStatus status,
                            Genres genres,
                            Set<VolumeResponse> volumes) {
}
