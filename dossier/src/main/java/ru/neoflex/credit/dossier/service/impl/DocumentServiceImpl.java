package ru.neoflex.credit.dossier.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.model.ClientDTO;
import ru.neoflex.credit.deal.model.CreditDTO;
import ru.neoflex.credit.deal.model.EmploymentDTO;
import ru.neoflex.credit.dossier.exception.IncorrectDocumentTypeException;
import ru.neoflex.credit.dossier.feign.DealFeignClient;
import ru.neoflex.credit.dossier.model.DocumentType;
import ru.neoflex.credit.dossier.service.abstracts.DocumentService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.neoflex.credit.dossier.model.DocumentType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DealFeignClient dealFeignClient;
    @Value("${document.text.contract}")
    private String CONTRACT_TEXT;
    @Value("${document.text.credit-application}")
    private String CREDIT_APPLICATION_TEXT;
    @Value("${document.text.loan-payment-schedule}")
    private String LOAN_PAYMENT_SCHEDULE_TEXT;

    @Override
    public File createDocument(DocumentType documentType, Map<String, String> data) {
        String textDocument;
        switch (documentType) {
            case CONTRACT:
                textDocument = CONTRACT_TEXT;
                break;
            case CREDIT_APPLICATION:
                textDocument = CREDIT_APPLICATION_TEXT;
                break;
            case LOAN_PAYMENT_SCHEDULE:
                textDocument = LOAN_PAYMENT_SCHEDULE_TEXT;
                break;
            default:
                String error = String.format("Incorrect document type: %s", documentType);
                log.error(error);
                throw new IncorrectDocumentTypeException(error);
        }
        log.info("Document type: {}\n Text document: {}", documentType, textDocument);
        StringSubstitutor stringSubstitutor = new StringSubstitutor(data);
        String textResult = stringSubstitutor.replace(textDocument);
        log.info("Document type: {}, textResult = {}", documentType, textResult);

        File file;
        try {
            Path path = Files.createTempFile(documentType.name().toLowerCase(), ".txt");
            file = path.toFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(textDocument);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Override
    public List<File> createAllDocuments(Long applicationId) {
        ApplicationDTO application = dealFeignClient.getApplicationById(applicationId);
        Map<String, String> data = writeData(application);

        File loanPaymentScheduleFile = createDocument(LOAN_PAYMENT_SCHEDULE, data);
        File creditApplicationFile = createDocument(CREDIT_APPLICATION, data);
        File loanAgreement = createDocument(CONTRACT, data);
        return List.of(
                loanAgreement, creditApplicationFile, loanPaymentScheduleFile
        );
    }

    private Map<String, String> writeData(ApplicationDTO application) {
        Map<String, String> data = new HashMap<>();
        CreditDTO credit = application.getCredit();
        ClientDTO client = application.getClient();
        EmploymentDTO employment = client.getEmployment();

        data.put("creditId", credit.getId().toString());
        data.put("creditDate", application.getCreationDate().toString());
        data.put("applicationCreationDate", application.getCreationDate().toString());
        String fullName = String.format("%s %s %s", client.getFirstName(), client.getMiddleName(), client.getLastName());
        data.put("clientFullName", fullName);
        String passport = String.format("%s %s issued data: %s, issue branch: %s",
                client.getPassportSeries(), client.getPassportNumber(),
                client.getPassportIssueDate().toString(), client.getPassportIssueBranch()
        );
        data.put("clientPassport", passport);
        data.put("creditAmount", credit.getAmount().toString());
        data.put("creditTerm", credit.getTerm().toString());
        data.put("monthlyPayment", credit.getMonthlyPayment().toString());
        data.put("rate", credit.getRate().toString());
        data.put("psk", credit.getPsk().toString());
        data.put("isInsuranceEnabled", credit.getIsInsuranceEnabled().toString());
        data.put("isSalaryClient", credit.getIsSalaryClient().toString());
        data.put("paymentSchedule", Arrays.deepToString(credit.getPaymentSchedule().toArray()));
        data.put("applicationId", application.getId().toString());
        data.put("clientBirthdate", client.getBirthdate().toString());
        data.put("clientGender", client.getGender());
        data.put("clientEmail", client.getEmail());
        data.put("clientMartialStatus", client.getMaritalStatus());
        data.put("clientDependentAmount", client.getDependentAmount().toString());
        data.put("employmentStatus", employment.getEmploymentStatus().name());
        data.put("employerINN", employment.getEmployerINN());
        data.put("employmentSalary", employment.getSalary().toString());
        data.put("employmentPosition", employment.getPosition().name());
        data.put("employmentWorkExperienceTotal", employment.getWorkExperienceTotal().toString());
        data.put("employmentWorkExperienceCurrent", employment.getWorkExperienceCurrent().toString());
        return data;
    }

}
