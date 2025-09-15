package fatecipi.progweb.mymanga.models.mappers;

import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.dtos.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    void mapOrder(OrderDto orderDto, @MappingTarget Order order);
}
