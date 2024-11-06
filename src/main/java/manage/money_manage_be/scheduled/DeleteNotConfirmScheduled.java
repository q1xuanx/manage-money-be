package manage.money_manage_be.scheduled;


import lombok.RequiredArgsConstructor;
import manage.money_manage_be.service.UsersService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeleteNotConfirmScheduled {
    private final UsersService usersService;
    @Scheduled(fixedRate = 10 * 60 * 500)
    public void deleteNotConfirm() {
        usersService.deleteUserRent();
    }
}
