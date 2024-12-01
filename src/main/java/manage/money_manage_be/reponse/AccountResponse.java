package manage.money_manage_be.reponse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {
    private String idAccount;
    private String nameAccount;
    private String emailAccount;
    private Double wallet;
}
