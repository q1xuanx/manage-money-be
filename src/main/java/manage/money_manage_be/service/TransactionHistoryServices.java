package manage.money_manage_be.service;

import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.TrainsactionHistory;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.reponse.HistoryResponse;
import manage.money_manage_be.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServices {
    private final TransactionHistoryRepository transactionHistoryRepository;
    public void addHistory(TrainsactionHistory transaction) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(7));
        transaction.setTimePay(now);
        transactionHistoryRepository.save(transaction);
    }
    public APIResponse getHistoryOfUser(String idAccount){
        List<TrainsactionHistory> list = transactionHistoryRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idAccount)).toList();
        List<HistoryResponse> responses = new ArrayList<>();
        for (TrainsactionHistory history : list) {
            HistoryResponse res = new HistoryResponse(history.getIdTransaction(),history.getNameUser(),history.getAccount().getIdAccount(),history.getBalance(),history.getTimePay());
            responses.add(res);
        }
        return new APIResponse(200, "get success", responses);
    }
}
