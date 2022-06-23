package ru.neoflex.credit.deal.mapper;

import org.mapstruct.Mapper;
import ru.neoflex.credit.deal.model.Client;
import ru.neoflex.credit.deal.model.ClientDTO;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDto(Client client);
}
