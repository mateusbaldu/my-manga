package fatecipi.progweb.mymanga.models.manga;

import fatecipi.progweb.mymanga.dto.manga.MangaCreateAndUpdateDto;
import fatecipi.progweb.mymanga.dto.manga.MangaResponseDto;
import fatecipi.progweb.mymanga.models.volume.VolumeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { VolumeMapper.class })
public interface MangaMapper {
    void mapManga(MangaCreateAndUpdateDto mangaCreateAndUpdateDto, @MappingTarget Manga manga);
    MangaResponseDto toMangaResponseDto(Manga manga);
}
