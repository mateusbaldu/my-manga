package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.models.dto.order.OrderCreate;
import fatecipi.progweb.mymanga.models.Order;
import fatecipi.progweb.mymanga.models.dto.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrderItemsMapper.class})
public interface OrderMapper {
    void mapOrder(OrderCreate orderCreate, @MappingTarget Order order);

    OrderResponse toOrderResponse(Order order);
}
