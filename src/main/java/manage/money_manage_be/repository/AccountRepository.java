package manage.money_manage_be.repository;

import manage.money_manage_be.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByUsername(String username);
}
