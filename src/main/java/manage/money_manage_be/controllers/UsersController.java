package manage.money_manage_be.controllers;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.CreateNewUserRequest;
import manage.money_manage_be.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UsersController {
    private final UsersService usersService;
    @PostMapping("/create")
    public APIResponse createNew(@RequestBody CreateNewUserRequest users) throws MessagingException {
        return usersService.createUser(users);
    }
    @GetMapping("/{idUser}")
    public APIResponse getUsers(@PathVariable String idUser) {
        return usersService.getALlUsers(idUser);
    }
    @PatchMapping("/update/{nameUser}/{total}")
    public APIResponse updateUser(@PathVariable String nameUser, @PathVariable Float total) throws MessagingException {
        return usersService.deleteMoneyLend(total, nameUser);
    }
    @GetMapping("/totals/{idUser}")
    public APIResponse getTotals(@PathVariable String idUser) {
        return usersService.totalDay(idUser);
    }
    @GetMapping("/remind/{idUser}")
    public APIResponse remind(@PathVariable String idUser) throws MessagingException {
        return usersService.remind(idUser);
    }
    @PostMapping("/confirm/{id}")
    public APIResponse confirm(@PathVariable String id) throws MessagingException {
        return usersService.confirmRent(id);
    }
}
