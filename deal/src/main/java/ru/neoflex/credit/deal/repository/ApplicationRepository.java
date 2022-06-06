package ru.neoflex.credit.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.credit.deal.model.Application;

@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {
}
