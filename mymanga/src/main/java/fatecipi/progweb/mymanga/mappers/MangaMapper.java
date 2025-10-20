package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.models.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.dto.manga.MangaUpdate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { VolumeMapper.class })
public interface MangaMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapCreateManga(MangaCreate mangaCreate, @MappingTarget Manga manga);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapUpdateManga(MangaUpdate mangaUpdate, @MappingTarget Manga manga);

    @Mapping(source = "volume", target = "volumes")
    MangaResponse toMangaResponseDto(Manga manga);
}
