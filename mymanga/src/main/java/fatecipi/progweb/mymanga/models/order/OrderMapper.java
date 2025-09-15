package fatecipi.progweb.mymanga.models.order;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    void mapOrder(OrderCreateDto orderCreateDto, @MappingTarget Order order);
}
