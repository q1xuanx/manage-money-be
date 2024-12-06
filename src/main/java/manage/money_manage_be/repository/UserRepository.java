package manage.money_manage_be.repository;

import manage.money_manage_be.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    List<Users> findByIsConfirmed(@Param("is_confirmed") int is_confirmed);
    List<Users> findByAccount_IdAccountAndIsConfirmedAndMoneyGreaterThan(@Param("account_id_account") String id_account, @Param("is_confirmed") int is_confirmed, float money);
}
