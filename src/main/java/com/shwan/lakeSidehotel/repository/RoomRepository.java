package com.shwan.lakeSidehotel.repository;

import com.shwan.lakeSidehotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM Room r " +
            "WHERE r.roomType LIKE %:roomType% " +
            "AND r.id NOT IN " +
            "(SELECT br.room.id FROM BookedRoom br " +
            "WHERE ((br.checkInDate <= :checkoutDate) " +
            "AND (br.checkOutDate >= :checkInDate)))")
    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkoutDate, String roomType);
}
