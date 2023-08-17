package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "PIX_TRANSACTION")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "version")
    private Double value;
    private String code;
    @Column(name = "transaction_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
    private String endToEnd;
}
