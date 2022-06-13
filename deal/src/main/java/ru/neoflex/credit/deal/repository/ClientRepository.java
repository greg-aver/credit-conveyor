package ru.neoflex.credit.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.credit.deal.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

}
