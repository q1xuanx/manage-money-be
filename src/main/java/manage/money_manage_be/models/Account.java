package manage.money_manage_be.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idAccount;
    private String username;
    private String password;
    private String fullName;
    private String email;
    @Column(columnDefinition = "integer default 0")
    private int isConfirm;
    private LocalDateTime dateSendConfirm;
    @ColumnDefault("0.0")
    private double wallet;
}
