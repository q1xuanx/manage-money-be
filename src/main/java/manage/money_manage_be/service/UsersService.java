package manage.money_manage_be.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.UserRepository;
import manage.money_manage_be.request.CreateNewUserRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final AccountService accountService;
    private final EmailServices emailServices;
    public APIResponse createUser(CreateNewUserRequest user) throws MessagingException {
        if (user.getNameUser().isEmpty()) {
            return new APIResponse(404, "name is empty", null);
        }
        Account account = accountService.getAccount(user.getIdAccount());
        if (account == null) {
            return new APIResponse(404, "account not found", null);
        }
        Users users = new Users();
        users.setNameUser(user.getNameUser());
        users.setEmail(user.getEmail());
        users.setMoney(user.getMoney());
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7));
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        users.setDateLend(localDateTime);
        users.setAccount(account);
        userRepository.save(users);
        emailServices.sendEmailConfirm(mailSender,users,account);
        return new APIResponse(200, "saved", user);
    }
    public APIResponse getALlUsers(String idAccount){
        List<Users> users = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idAccount) && s.getIsConfirmed() == 1 && s.getMoney() > 0).toList();
        return new APIResponse(200, "list users", users);
    }
    public APIResponse totalDay(String idAccount){
        List<Users> users = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idAccount) && s.getIsConfirmed() == 1 && s.getMoney() > 0).toList();
        double total = users.stream().mapToDouble(s -> (double) s.getMoney()).sum();
        return new APIResponse(200, "total day", total);
    }
    public APIResponse remind(String idUser) throws MessagingException {
        Optional<Users> find = userRepository.findById(idUser);
        if (find.isPresent()) {
            Users user = find.get();
            emailServices.sendRemindEmail(mailSender,user);
            return new APIResponse(200, "remind", user);
        }
        return new APIResponse(404, "not found", null);
    }
    public APIResponse deleteMoneyLend (float money, String nameUser) throws MessagingException {
        List<Users> list = userRepository.findAll();
        Optional<Users> findExist = list.stream().filter(s -> s.getId().equals(nameUser)).findFirst();
        if (findExist.isPresent()) {
            Users user = findExist.get();
            user.setMoney(user.getMoney() - money);
            userRepository.save(user);
            emailServices.sendPaymentEmail(user,mailSender,money);
            return new APIResponse(200,"success", "ok da tru " + money);
        }
        return new APIResponse(404, "not found", null);
    }
    public APIResponse confirmRent(String idRents) throws MessagingException {
        Optional<Users> getUser = userRepository.findById(idRents);
        if (getUser.isPresent()) {
            Users user = getUser.get();
            if (user.getIsConfirmed() == 1){
                return new APIResponse(404, "wrong", null);
            }
            user.setIsConfirmed(1);
            userRepository.save(user);
            emailServices.sendEmailSuccessRent(mailSender,user);
            return new APIResponse(200, "confirm", user);
        }
        return new APIResponse(404, "not found", null);
    }
    public void deleteUserRent () {
        List<Users> listUser = userRepository.findAll();
        Iterator<Users> iterator = listUser.iterator();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7));
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        int count = 0;
        while (iterator.hasNext()) {
            Users user = iterator.next();
            if (user.getIsConfirmed() == 0) {
                Duration duration = Duration.between(user.getDateLend(), localDateTime);
                if (duration.toMinutes() > 5) {
                    userRepository.delete(user);
                    count++;
                }
            }
        }
        System.out.println("Task complete, remove: " + count + " users");
    }
    public APIResponse totalOfUserHaveRent(String idUser){
        List<Users> list = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idUser) && s.getIsConfirmed() == 1).toList();
        Map<String, Float> totalRent = new HashMap<>();
        for (Users user : list) {
            if (!totalRent.containsKey(user.getNameUser())){
                totalRent.put(user.getNameUser().trim().toLowerCase(), user.getMoney());
            }else {
                totalRent.put(user.getNameUser().trim().toLowerCase(), totalRent.get(user.getNameUser()) + user.getMoney());
            }
        }
        return new APIResponse(200, "success", totalRent);
    }
}
