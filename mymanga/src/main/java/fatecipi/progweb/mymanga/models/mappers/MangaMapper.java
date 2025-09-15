package fatecipi.progweb.mymanga.models.mappers;

import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.dtos.MangaDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MangaMapper {
    void mapManga(MangaDto mangaDto, @MappingTarget Manga manga);
}
