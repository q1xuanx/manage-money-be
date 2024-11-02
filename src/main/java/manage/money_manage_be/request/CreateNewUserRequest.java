package manage.money_manage_be.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNewUserRequest {
    private String nameUser;
    private float money;
    private String email;
    private String idAccount;
}
