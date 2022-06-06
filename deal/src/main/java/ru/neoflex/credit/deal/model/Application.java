package ru.neoflex.credit.deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(fluent = true, chain = true)
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SequenceGenerator(name = "applicationSeqGenerator", sequenceName = "application_id_seq", allocationSize = 1)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicationSeqGenerator")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum status;

    @Column
    private LocalDate creationDate;

    @Column
    private LocalDate signDate;

    @Column
    private Integer sesCode;

    @Column
    @Type(type = "jsonb")
    private List<ApplicationStatusHistoryDTO> statusHistory;

    @Column
    @Type(type = "jsonb")
    private LoanOfferDTO appliedOffer;
}