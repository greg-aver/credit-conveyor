package ru.neoflex.credit.deal.mapper;

import org.mapstruct.Mapper;
import ru.neoflex.credit.deal.model.Client;
import ru.neoflex.credit.deal.model.ClientDTO;

@Mapper(componentModel = "spring")
public abstract class ClientMapper {
    public ClientDTO toDto(Client client) {
        return new ClientDTO()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .account(client.getAccount())
                .employment(client.getEmploymentDTO())
                .birthdate(client.getBirthDate())
                .dependentAmount(client.getDependentAmount())
                .email(client.getEmail())
                .gender(client.getGender())
                .maritalStatus(client.getMartialStatus())
                .middleName(client.getMiddleName())
                .passportIssueBranch(client.getPassport().getIssueBranch())
                .passportIssueDate(client.getPassport().getIssueDate())
                .passportNumber(client.getPassport().getNumber())
                .passportSeries(client.getPassport().getSeries());
    }
}
