package manage.money_manage_be.service;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.auth.CustomAccountDetails;
import manage.money_manage_be.auth.JwtTokenProvider;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.AccountRepository;
import manage.money_manage_be.request.CreateAccountRequest;
import manage.money_manage_be.request.LoginRequest;
import manage.money_manage_be.reponse.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServices emailServices;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findById(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomAccountDetails(account.get());
    }
    public APIResponse register(CreateAccountRequest createAccountRequest) {
        if (accountRepository.findByUsername(createAccountRequest.getUserName()) != null) {
            return new APIResponse(400, "username exist", null);
        }
        if (createAccountRequest.getEmail() == null || accountRepository.findByEmail(createAccountRequest.getEmail()) != null) {
            return new APIResponse(400, "email is required || email exist", null);
        }
        Account account = new Account();
        account.setEmail(createAccountRequest.getEmail());
        account.setUsername(createAccountRequest.getUserName());
        account.setPassword(passwordEncoder.encode(createAccountRequest.getPassword()));
        account.setFullName(createAccountRequest.getFullName());
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7));
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        account.setDateSendConfirm(localDateTime);
        accountRepository.save(account);
        emailServices.confirmAccount(mailSender, account);
        return new APIResponse(200, "success please confirm your email", "saved please back to login");
    }
    public void updateWallet(Account account){
        accountRepository.save(account);
    }
    public APIResponse login(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new APIResponse(400, "username or password incorrect", null);
        }
        Account account = accountRepository.findByUsername(loginRequest.getUsername());
        if (account == null) {
            return new APIResponse(400, "username or password incorrect", null);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(),account.getPassword())) {
            return new APIResponse(400, "password does not match", null);
        }
        if (account.getIsConfirm() == 0) {
            LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.ofHours(7));
            if(account.getDateSendConfirm() == null){
                emailServices.confirmAccount(mailSender, account);
                account.setDateSendConfirm(localDateTime);
                accountRepository.save(account);
                return new APIResponse(200, "please confirm your email again", null);
            }
            Duration duration = Duration.between(account.getDateSendConfirm(), localDateTime);
            if (duration.toMinutes() > 5)
            {
                account.setDateSendConfirm(localDateTime);
                accountRepository.save(account);
                emailServices.confirmAccount(mailSender, account);
                return new APIResponse(200, "please confirm your email again", null);
            }
            return new APIResponse(400, "confirmation is required", null);
        }
        String token = jwtTokenProvider.generateToken(new CustomAccountDetails(account));
        return new APIResponse(200, token, account.getIdAccount());
    }
    public APIResponse listAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return new APIResponse(200, "success", accounts);
    }
    public APIResponse confirmEmail(String idAccount){
        Optional<Account> account = accountRepository.findById(idAccount);
        if (account.isPresent() && account.get().getIsConfirm() == 0) {
            account.get().setIsConfirm(1);
            accountRepository.save(account.get());
            return new APIResponse(200, "confirm", account);
        }
        return new APIResponse(400, "account does not exist or is confirmed", null);
    }
    public Account getAccount(String id) {
        return accountRepository.findById(id).orElse(null);
    }
    public APIResponse getInformation(String id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            return new APIResponse(400, "account does not exist or is confirmed", null);
        }
        AccountResponse accountResponse = new AccountResponse(account.getIdAccount(),account.getFullName(),account.getEmail(),account.getWallet());
        return new APIResponse(200, "success", accountResponse);
    }
}
