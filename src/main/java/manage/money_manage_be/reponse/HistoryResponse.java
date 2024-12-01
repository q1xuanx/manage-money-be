package manage.money_manage_be.reponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class HistoryResponse {
    private int idTransaction;
    private String namePay;
    private String account;
    private double amount;
    private LocalDateTime date;
}
