package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingDto.setItem(new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()));
        return bookingDto;
    }

    public static Booking toBooking(BookingRequestDto bookingJsonDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setEnd(bookingJsonDto.getEnd());
        booking.setStart(bookingJsonDto.getStart());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static ItemResponseDto.BookingItemDto toBookingItemDto(Booking booking) {
        ItemResponseDto.BookingItemDto bookingDto = new ItemResponseDto.BookingItemDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }
}
