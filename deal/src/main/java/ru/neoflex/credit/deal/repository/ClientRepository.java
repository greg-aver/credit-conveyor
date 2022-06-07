package ru.neoflex.credit.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.credit.deal.model.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

}
