package fatecipi.progweb.mymanga.models.volume;

import fatecipi.progweb.mymanga.dto.volume.VolumeCreateDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponseDto;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VolumeMapper {
    void mapCreateVolume(VolumeCreateDto volumeCreateDto, @MappingTarget Volume volume);
    void mapUpdateVolume(VolumeUpdateDto volumeUpdateDto, @MappingTarget Volume volume);
    @Mapping(source = "manga.id", target = "mangaId")
    @Mapping(source = "manga.title", target = "mangaTitle")
    VolumeResponseDto toVolumeResponseDto(Volume volume);
}
