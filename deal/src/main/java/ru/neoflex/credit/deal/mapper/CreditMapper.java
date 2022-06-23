package ru.neoflex.credit.deal.mapper;

import org.mapstruct.Mapper;
import ru.neoflex.credit.deal.model.Credit;
import ru.neoflex.credit.deal.model.CreditDTO;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    CreditDTO toDto(Credit credit);
}
