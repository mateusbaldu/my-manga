package fatecipi.progweb.mymanga.configs.mappers;

import fatecipi.progweb.mymanga.models.dto.manga.MangaCreateAndUpdate;
import fatecipi.progweb.mymanga.models.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.Manga;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { VolumeMapper.class })
public interface MangaMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapManga(MangaCreateAndUpdate mangaCreateAndUpdate, @MappingTarget Manga manga);

    @Mapping(source = "volume", target = "volumes")
    MangaResponse toMangaResponseDto(Manga manga);
}
