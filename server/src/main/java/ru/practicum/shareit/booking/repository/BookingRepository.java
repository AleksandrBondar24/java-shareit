package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime date, PageRequest page);

    Page<Booking> findAllByBooker_Id(Long bookerId, PageRequest page);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime date, LocalDateTime date1, PageRequest page);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime date, PageRequest page);

    Page<Booking> findAllByItem_IdIn(List<Long> itemId, PageRequest page);

    Page<Booking> findByItem_IdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, PageRequest page);

    Page<Booking> findByItem_IdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, PageRequest page);

    Page<Booking> findByItem_IdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, BookingStatus bookingStatus, PageRequest page);

    Page<Booking> findByBooker_IdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, BookingStatus bookingStatus, PageRequest page);

    Page<Booking> findByItem_IdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, PageRequest page);

    List<Booking> findAllByItem_IdInAndStatusIs(List<Long> itemId, BookingStatus status);

    List<Booking> findByItem_IdAndStatusIs(Long itemId, BookingStatus status);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime date);

    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long userId);
}
