package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.dto.volume.VolumeCreate;
import fatecipi.progweb.mymanga.dto.volume.VolumeResponse;
import fatecipi.progweb.mymanga.dto.volume.VolumeUpdate;
import fatecipi.progweb.mymanga.models.Volume;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VolumeMapper {
    void createMapping(VolumeCreate volumeCreate, @MappingTarget Volume volume);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMapping(VolumeUpdate volumeUpdate, @MappingTarget Volume volume);

    @Mapping(source = "manga.id", target = "mangaId")
    @Mapping(source = "manga.title", target = "mangaTitle")
    VolumeResponse responseMapping(Volume volume);
}
