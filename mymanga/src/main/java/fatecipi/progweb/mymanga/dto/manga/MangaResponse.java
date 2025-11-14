package fatecipi.progweb.mymanga.dto.manga;

import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;

import java.util.Set;

public record MangaResponse(Long id,
                            String title,
                            String author,
                            String description,
                            Double rating,
                            String keywords,
                            MangaStatus status,
                            Genres genres,
                            Set<VolumeResponse> volumes,
                            String imageUrl) {
}
