package ru.neoflex.credit.deal.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.neoflex.credit.deal.model.Application;
import ru.neoflex.credit.deal.model.ApplicationDTO;
@Mapper(componentModel = "spring")

public abstract class ApplicationMapper {
    @Autowired
    private CreditMapper creditMapper;
    @Autowired
    private ClientMapper clientMapper;

    public ApplicationDTO toDto(Application application) {
        return new ApplicationDTO()
                .client(clientMapper.toDto(application.client()))
                .credit(creditMapper.toDto(application.credit()))
                .id(application.id())
                .creationDate(application.creationDate().atStartOfDay())
//                .sesCode(application.sesCode().toString())
                .status(application.status())
                .statusHistory(application.statusHistory());
    }
}
