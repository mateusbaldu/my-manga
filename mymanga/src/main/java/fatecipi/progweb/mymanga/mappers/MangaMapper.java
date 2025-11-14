package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.dto.manga.MangaCardResponse;
import fatecipi.progweb.mymanga.dto.manga.MangaCreate;
import fatecipi.progweb.mymanga.dto.manga.MangaResponse;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.dto.manga.MangaUpdate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { VolumeMapper.class })
public interface MangaMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void createMapping(MangaCreate mangaCreate, @MappingTarget Manga manga);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "volume", ignore = true)
    void updateMapping(MangaUpdate mangaUpdate, @MappingTarget Manga manga);

    @Mapping(source = "volume", target = "volumes")
    MangaResponse responseMapping(Manga manga);

    MangaCardResponse toMangaCardResponse(Manga manga);
}
