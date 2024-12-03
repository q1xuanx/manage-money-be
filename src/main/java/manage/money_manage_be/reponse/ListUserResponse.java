package manage.money_manage_be.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class ListUserResponse {
    public List<UserResponse> listUsers;
    public float totalsValue;
}
