package ru.neoflex.credit.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.credit.deal.model.Credit;

@Repository
public interface CreditRepository extends CrudRepository<Credit, Long> {
}
