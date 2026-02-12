package com.jetrental.repository;

import com.jetrental.entity.Jet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.*;

@Repository
public class JetRepositoryImpl implements JetRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Jet> searchJets(int minSeats, int maxSeats, double maxPrice, String brand, String model) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Jet> cq = cb.createQuery(Jet.class);
        Root<Jet> jet = cq.from(Jet.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(jet.get("seats"), minSeats, maxSeats));

        if (maxPrice > 0) {
            predicates.add(cb.lessThanOrEqualTo(jet.get("pricePerDay"), maxPrice));
        }
        if (brand != null && !brand.isEmpty()) {
            predicates.add(cb.equal(jet.get("brand"), brand));
        }
        if (model != null && !model.isEmpty()) {
            predicates.add(cb.equal(jet.get("model"), model));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }
}