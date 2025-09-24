package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotItemOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь c id %d не найдена", itemId)));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование c id %d не найдено", bookingId)));
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, NewBookingRequest request) {
        log.info("POST /bookings - создание брони");
        User user = findUserById(userId);
        Item item = findItemById(request.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования!");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Владелец вещи не может забронировать свою же вещь");
        }

        Booking booking = BookingMapper.INSTANCE.mapToBooking(request, user, item);

        bookingRepository.save(booking);

        return BookingMapper.INSTANCE.mapToBookingDto(booking);

    }

    @Override
    public BookingDto findBooking(Long bookingId, Long userId) {
        log.info("GET /bookings/{booking-id} - получение бронирования");
        Booking booking = findBookingById(bookingId);
        User user = findUserById(booking.getItem().getOwner().getId());

        if (!booking.getBooker().getId().equals(userId) && !user.getId().equals(userId)) {
            throw new ValidationException("Просмотр возможен либо автором бронирования, либо владельцем вещи");
        }

        return BookingMapper.INSTANCE.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByUser(Long userId, String state) {
        log.info("GET /bookings?state={state} - получение списка всех бронирований текущего пользователя");
        States currentState = States.valueOf(state);
        User user = findUserById(userId);
        List<Booking> bookingsList = new ArrayList<>();
        switch (currentState) {
            case ALL:
                bookingsList = bookingRepository.findAllByBookerId(userId);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllCurrentBookingByBookerId(userId);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllPastBookingByBookerId(userId);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllFutureBookingByBookerId(userId);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllByBookerIdAndStatus(userId, Statuses.WAITING);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllByBookerIdAndStatus(userId, Statuses.REJECTED);
                break;
        }
        return bookingsList.stream()
                .map(BookingMapper.INSTANCE::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .toList();
    }

    @Override
    public List<BookingDto> findAllBookingsByOwnerItems(Long userId, String state) {
        log.info("GET /bookings/owner?state={state} - получение списка бронирований для всех вещей текущего пользователя");
        States currentState = States.valueOf(state);
        User user = findUserById(userId);
        List<Booking> bookingsList = new ArrayList<>();
        switch (currentState) {
            case ALL:
                bookingsList = bookingRepository.findAllByOwnerId(userId);
                break;
            case CURRENT:
                bookingsList = bookingRepository.findAllCurrentBookingByOwnerId(userId);
                break;
            case PAST:
                bookingsList = bookingRepository.findAllPastBookingByOwnerId(userId);
                break;
            case FUTURE:
                bookingsList = bookingRepository.findAllFutureBookingByOwnerId(userId);
                break;
            case WAITING:
                bookingsList = bookingRepository.findAllByOwnerIdAndStatus(userId, Statuses.WAITING);
                break;
            case REJECTED:
                bookingsList = bookingRepository.findAllByOwnerIdAndStatus(userId, Statuses.REJECTED);
                break;
        }
        return bookingsList.stream()
                .map(BookingMapper.INSTANCE::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .toList();
    }

    @Override
    public BookingDto updateBooking(Long userId, UpdateBookingRequest request) {
        log.info("PUT /bookings - обновление бронирования");
        if (request.getId() == null) {
            throw new ValidationException("id бронирования должен быть указан");
        }
        User owner = findUserById(userId);
        Booking booking = findBookingById(request.getId());

        if (!booking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new ValidationException("Просмотр возможен либо автором бронирования, либо владельцем вещи");
        }
        BookingMapper.INSTANCE.updateBookingFromRequest(request, booking);
        booking = bookingRepository.save(booking);
        return BookingMapper.INSTANCE.mapToBookingDto(booking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        log.info("DELETE /bookings/{booking-id} - удаление бронирования");
        Booking booking = findBookingById(bookingId);
        bookingRepository.delete(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("PATCH /bookings/{booking-id}?approved={approved} - подтверждение или отклонение запроса на бронирование");
        Booking booking = findBookingById(bookingId);
        Item item = findItemById(booking.getItem().getId());

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotItemOwnerException("Менять статус вещи может только её владелец");
        }

        if (!booking.getStatus().equals(Statuses.WAITING)) {
            throw new ValidationException("Вещь уже забронирована");
        }

        booking.setStatus(approved ? Statuses.APPROVED : Statuses.REJECTED);
        return BookingMapper.INSTANCE.mapToBookingDto(booking);
    }
}
