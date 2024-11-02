package manage.money_manage_be.controllers;


import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.LoginRequest;
import manage.money_manage_be.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/login")
    public APIResponse loginController(@RequestBody LoginRequest loginRequest) {
        return accountService.login(loginRequest);
    }
    @PostMapping("/register")
    public APIResponse registerController(@RequestBody Account account){
        return accountService.register(account);
    }
}
