package fatecipi.progweb.mymanga.configs.mappers;

import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.Manga;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { VolumeMapper.class })
public interface MangaMapper {
    void mapManga(MangaCreateAndUpdate mangaCreateAndUpdate, @MappingTarget Manga manga);
    MangaResponse toMangaResponseDto(Manga manga);
}
