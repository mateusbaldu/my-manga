package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.dto.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrderItemsMapper.class})
public interface OrderMapper {
    void createMapping(OrderCreate orderCreate, @MappingTarget Order order);

    @Mapping(source = "users.username", target = "username")
    OrderResponse toOrderResponse(Order order);
}
