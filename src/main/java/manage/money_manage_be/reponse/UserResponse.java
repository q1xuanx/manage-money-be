package manage.money_manage_be.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String nameUser;
    private float money;
    private String email;
    private LocalDateTime dateLend;
}
