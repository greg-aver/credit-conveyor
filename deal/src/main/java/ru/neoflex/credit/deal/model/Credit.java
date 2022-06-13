package ru.neoflex.credit.deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Accessors(chain = true)
@SequenceGenerator(name = "creditSeqGenerator", sequenceName="credit_id_seq", allocationSize = 1)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "creditSeqGenerator")
    private Long id;

    @Column
    private BigDecimal amount;

    @Column
    private Integer term;

    @Column
    private BigDecimal monthlyPayment;

    @Column
    private BigDecimal rate;

    @Column
    private BigDecimal psk;

    @Column
    @Type(type = "jsonb")
    private List<PaymentScheduleElement> paymentSchedule;

    @Column
    private Boolean isInsuranceEnabled;

    @Column
    private Boolean isSalaryClient;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Application application;

    @Enumerated(EnumType.STRING)
    CreditStatus creditStatus;
}
