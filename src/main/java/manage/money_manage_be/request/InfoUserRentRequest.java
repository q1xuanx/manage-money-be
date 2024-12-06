package manage.money_manage_be.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InfoUserRentRequest {
    public String nameUser;
    public float money;
    public String emailUser;
}
