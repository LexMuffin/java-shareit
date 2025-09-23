package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "item", target = "item", qualifiedByName = "mapToItemDto")
    @Mapping(source = "booker", target = "booker", qualifiedByName = "mapToUserDto")
    @Mapping(source = "status", target = "status")
    BookingDto mapToBookingDto(Booking booking);

    @Named("mapToItemDto")
    default ItemDto mapToItemDto(Item item) {
        return ItemMapper.INSTANCE.mapToItemDto(item);
    }

    @Named("mapToUserDto")
    default UserDto mapToUserDto(User user) {
        return UserMapper.mapToUserDto(user);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.start", target = "start")
    @Mapping(source = "request.end", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(target = "status", expression = "java(ru.practicum.shareit.enums.Statuses.WAITING)")
    Booking mapToBooking(NewBookingRequest request, User booker, Item item);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    void updateBookingFromRequest(UpdateBookingRequest updateRequest, @MappingTarget Booking booking);

    List<BookingDto> toDtoList(List<Booking> bookings);
}