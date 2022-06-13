package ru.neoflex.credit.deal.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Accessors(chain = true)
@SequenceGenerator(name = "clientSeqGenerator", sequenceName = "client_id_seq", allocationSize = 1)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientSeqGenerator")
    private Long id;

    @Column
    private String lastName;

    @Column
    private String firstName;

    @Column
    private String middleName;

    @Column
    private LocalDate birthDate;

    @Column
    private String email;

    @Column
    private String gender;

    @Column
    private String martialStatus;

    @Column
    private Integer dependentAmount;

    @Column
    @Type(type = "jsonb")
    private Passport passport;

    @Column(name = "employment_dto")
    @Type(type = "jsonb")
    private EmploymentDTO employmentDTO;

    @Column
    private String account;

    @Column
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Application application;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Credit credit;
}
