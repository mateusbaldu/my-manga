package fatecipi.progweb.mymanga.configs.mappers;

import fatecipi.progweb.mymanga.models.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.models.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.models.Volume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VolumeMapper {
    void mapCreateVolume(VolumeCreate volumeCreate, @MappingTarget Volume volume);
    void mapUpdateVolume(VolumeUpdate volumeUpdate, @MappingTarget Volume volume);
    @Mapping(source = "manga.id", target = "mangaId")
    @Mapping(source = "manga.title", target = "mangaTitle")
    VolumeResponse toVolumeResponseDto(Volume volume);
}
