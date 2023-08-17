package santander.cloud.sap.models;

import lombok.*;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DebitAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long branch;
    private Long number;

    public DebitAccount(Long branch, Long number) {
        this.branch = branch;
        this.number = number;
    }
}
