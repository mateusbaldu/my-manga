package fatecipi.progweb.mymanga.models.volume;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VolumeMapper {
    void mapCreateVolume(VolumeCreateDto volumeCreateDto, @MappingTarget Volume volume);
    void mapUpdateVolume(VolumeUpdateDto volumeUpdateDto, @MappingTarget Volume volume);
}
