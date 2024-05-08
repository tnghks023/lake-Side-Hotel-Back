package com.shwan.lakeSidehotel.service;

import com.shwan.lakeSidehotel.exception.InvalidBookingRequestException;
import com.shwan.lakeSidehotel.exception.ResourceNotFoundException;
import com.shwan.lakeSidehotel.model.BookedRoom;
import com.shwan.lakeSidehotel.model.Room;
import com.shwan.lakeSidehotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<BookedRoom> getBookingsByUserEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);

    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore((bookingRequest.getCheckInDate()))) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Optional<Room> optionalRoom = roomService.getRoomById(roomId);
        if (optionalRoom.isEmpty()) {
            throw new IllegalArgumentException("Room with ID " + roomId + " not found");
        }

        Room room = optionalRoom.get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);

        if (roomIsAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
            return bookingRequest.getBookingConfirmationCode();
        } else {
            throw new InvalidBookingRequestException("Sorry, This room is not available for the selected dates");
        }

    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(()-> new ResourceNotFoundException("No booking found with booking code  : " +confirmationCode));
    }



    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        !bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckInDate()) &&
                                !bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckOutDate())
                );
    }

}
