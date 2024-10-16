package manage.money_manage_be.controllers;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    @PostMapping("/create")
    public APIResponse createNew(@RequestBody Users users) throws MessagingException {
        return usersService.createUser(users);
    }
    @GetMapping("/")
    public APIResponse getUsers() {
        return usersService.getALlUsers();
    }
    @PutMapping("/update/{nameUser}/{total}")
    public APIResponse updateUser(@PathVariable String nameUser, @PathVariable Float total) throws MessagingException {
        return usersService.deleteMoneyLend(total, nameUser);
    }
    @GetMapping("/totals")
    public APIResponse getTotals() {
        return usersService.totalDay();
    }
    @GetMapping("/remind/{idUser}")
    public APIResponse remind(@PathVariable String idUser) throws MessagingException {
        return usersService.remind(idUser);
    }

}
