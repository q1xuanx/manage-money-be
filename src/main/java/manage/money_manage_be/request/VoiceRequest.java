package manage.money_manage_be.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceRequest {
    private String text;
    private String idAccount;
}
