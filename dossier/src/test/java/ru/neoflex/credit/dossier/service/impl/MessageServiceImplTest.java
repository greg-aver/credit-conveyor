package ru.neoflex.credit.dossier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import ru.neoflex.credit.deal.model.ApplicationDTO;
import ru.neoflex.credit.deal.model.EmailMessage;
import ru.neoflex.credit.deal.model.MessageKafka;
import ru.neoflex.credit.dossier.feign.DealFeignClient;
import ru.neoflex.credit.dossier.sender.abstracts.SenderEmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.neoflex.credit.deal.model.MessageKafka.ThemeEnum.FINISH_REGISTRATION;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SenderEmailService senderEmailService;
    @Mock
    private DealFeignClient dealFeignClient;
    @InjectMocks
    private MessageServiceImpl messageService;

    private MessageKafka messageKafka;

    @Before
    public void setUp() {
        messageKafka = new MessageKafka()
                .address("konveierov@yandex.ru")
                .applicationId(300L)
                .theme(FINISH_REGISTRATION);
    }


    @Test
    public void getMessageFromJson() throws JsonProcessingException {
        String messageJson = "{\"address\": \"konveierov@yandex.ru\",\n" +
                "  \"applicationId\": 300,\n" +
                "  \"theme\": \"FINISH_REGISTRATION\"\n" +
                "}";
        when(objectMapper.readValue(messageJson, MessageKafka.class)).thenReturn(messageKafka);
        MessageKafka messageKafkaActual = messageService.getMessageFromJson(messageJson);
        assertEquals(messageKafka, messageKafkaActual);
        verify(objectMapper, times(1)).readValue(messageJson, MessageKafka.class);
    }

    @Test
    public void sendMessage() {
        String subject = "Finish registration";
        String text = "Loan application 300 approved! Follow this link https://LINK";
        EmailMessage emailMessageExpected = new EmailMessage()
                .address(messageKafka.getAddress())
                .text(text)
                .subject(subject);
        ApplicationDTO applicationDTO = new ApplicationDTO()
                .id(300L);
        ReflectionTestUtils.setField(messageService, "FINISH_REGISTRATION_SUBJECT", "Finish registration");
        ReflectionTestUtils.setField(messageService, "FINISH_REGISTRATION_TEXT", "Loan application {applicationId} approved! Follow this link https://LINK");

        when(dealFeignClient.getApplicationById(anyLong())).thenReturn(applicationDTO);
        messageService.sendMessage(messageKafka);
        verify(senderEmailService, times(1)).sendMessage(any());
    }

    @Test
    public void kafkaMessageToEmailMessage() {
        String subject = "Finish registration";
        String text = "Loan application 300 approved! Follow this link https://LINK";
        EmailMessage emailMessageExpected = new EmailMessage()
                .address(messageKafka.getAddress())
                .text(text)
                .subject(subject);
        ApplicationDTO applicationDTO = new ApplicationDTO()
                .id(300L);
        ReflectionTestUtils.setField(messageService, "FINISH_REGISTRATION_SUBJECT", "Finish registration");
        ReflectionTestUtils.setField(messageService, "FINISH_REGISTRATION_TEXT", "Loan application {applicationId} approved! Follow this link https://LINK");
        when(dealFeignClient.getApplicationById(anyLong())).thenReturn(applicationDTO);
        EmailMessage emailMessageActual = messageService.kafkaMessageToEmailMessage(messageKafka);
        assertEquals(emailMessageExpected, emailMessageActual);
    }
}
