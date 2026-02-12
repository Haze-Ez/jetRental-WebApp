package com.jetrental.repository;

import com.jetrental.entity.Jet;
import java.util.List;

public interface JetRepositoryCustom {
    List<Jet> searchJets(int minSeats, int maxSeats, double maxPrice, String brand, String model);
}