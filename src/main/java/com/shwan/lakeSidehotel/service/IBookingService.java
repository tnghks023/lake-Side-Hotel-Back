package com.shwan.lakeSidehotel.service;

import com.shwan.lakeSidehotel.model.BookedRoom;

import java.util.List;

public interface IBookingService {
    void cancelBooking(Long bookingId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    List<BookedRoom> getBookingsByUserEmail(String email);
}
