package fatecipi.progweb.mymanga.mappers;

import fatecipi.progweb.mymanga.models.OrderItems;
import fatecipi.progweb.mymanga.models.dto.order.OrderItemsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemsMapper {
    @Mapping(source = "title", target = "mangaTitle")
    @Mapping(target = "volumeNumber", expression = "java(extractVolumeNumber(orderItems.getTitle()))")
    OrderItemsResponse responseMapping(OrderItems orderItems);

    default Integer extractVolumeNumber(String title) {
        if (title != null && title.contains("Vol. ")) {
            try {
                String volumePart = title.substring(title.lastIndexOf("Vol. ") + 5);
                return Integer.parseInt(volumePart.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
