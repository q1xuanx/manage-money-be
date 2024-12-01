package manage.money_manage_be.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TrainsactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTransaction;
    private String nameUser;
    @ManyToOne
    private Account account;
    private double balance;
    private LocalDateTime timePay;
}
