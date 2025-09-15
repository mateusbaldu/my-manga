package fatecipi.progweb.mymanga.models.manga;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MangaMapper {
    void mapManga(MangaCreateDto mangaCreateDto, @MappingTarget Manga manga);
}
