package com.shwan.lakeSidehotel.controller;


import com.shwan.lakeSidehotel.exception.PhotoRetrievalException;
import com.shwan.lakeSidehotel.exception.ResourceNotFoundException;
import com.shwan.lakeSidehotel.model.BookedRoom;
import com.shwan.lakeSidehotel.model.Room;
import com.shwan.lakeSidehotel.response.BookingResponse;
import com.shwan.lakeSidehotel.response.RoomResponse;
import com.shwan.lakeSidehotel.service.BookingService;
import com.shwan.lakeSidehotel.service.IBookingService;
import com.shwan.lakeSidehotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final IRoomService roomService;
    private final IBookingService bookingService;

    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) {
        try {
            Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
            RoomResponse response = new RoomResponse(savedRoom.getId(),
                    savedRoom.getRoomType(), savedRoom.getRoomPrice());
            return ResponseEntity.ok(response);
        } catch (SQLException | IOException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @GetMapping("/room-types")
    public ResponseEntity<List<String>> getRoomTypes() {
        List<String> roomTypes = roomService.getRoomTypes();
        return ResponseEntity.ok(roomTypes);
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("/delete/room/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                                @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : availableRooms) {
            //byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());

            RoomResponse roomResponse = getRoomResponse(room);

            roomResponses.add(roomResponse);
        }

        if (roomResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomResponses);
        }

    }


    private RoomResponse getRoomResponse(Room room) {

        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo = bookings
//                .stream()
//                .map(booking -> new BookingResponse(booking.getBookingId(),
//                        booking.getCheckInDate(),
//                        booking.getCheckOutDate(),
//                        booking.getBookingConfirmationCode()))
//                .toList();

        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();

        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());

            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo " + e);
            }
        }
        return new RoomResponse(room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(), photoBytes);
    }


    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

    private byte[] blobPhotoToBytes(Blob photoBlob) throws SQLException {
        if (photoBlob != null) {
            int blobLength = (int) photoBlob.length();
            return photoBlob.getBytes(1, blobLength);
        }
        return null;
    }

}
