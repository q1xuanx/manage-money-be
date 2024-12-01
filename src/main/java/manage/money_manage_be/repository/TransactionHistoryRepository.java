package manage.money_manage_be.repository;
import manage.money_manage_be.models.TrainsactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TrainsactionHistory, Integer> {
}
