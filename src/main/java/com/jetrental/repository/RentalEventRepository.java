package com.jetrental.repository;

import com.jetrental.entity.RentalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalEventRepository extends JpaRepository<RentalEvent, Integer> {
    @Query("SELECT r FROM RentalEvent r WHERE r.customerRenting.id = :customerId")
    List<RentalEvent> findByCustomerId(@Param("customerId") int customerId);
}