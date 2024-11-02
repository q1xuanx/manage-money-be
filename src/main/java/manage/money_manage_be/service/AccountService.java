package manage.money_manage_be.service;

import lombok.RequiredArgsConstructor;
import manage.money_manage_be.configuation.SecurityConfig;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.AccountRepository;
import manage.money_manage_be.request.LoginRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findById(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomAccountDetails(account.get());
    }
    public APIResponse register(Account account) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            return new APIResponse(400, "username exist", null);
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account saved = accountRepository.save(account);
        return new APIResponse(200, "success", saved);
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
        String token = jwtTokenProvider.generateToken(new CustomAccountDetails(account));
        return new APIResponse(200, token, account.getIdAccount());
    }
    public Account getAccount(String id) {
        return accountRepository.findById(id).orElse(null);
    }
}
