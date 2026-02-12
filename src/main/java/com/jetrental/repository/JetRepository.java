package com.jetrental.repository;

import com.jetrental.entity.Jet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JetRepository extends JpaRepository<Jet, Integer>, JetRepositoryCustom {
    List<Jet> findByBrand(String brand);
}