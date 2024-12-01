package manage.money_manage_be.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.configuation.VnPayConfig;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.TrainsactionHistory;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.UserRepository;
import manage.money_manage_be.request.CreateNewUserRequest;
import manage.money_manage_be.request.VoiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.awt.geom.QuadCurve2D;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailServices emailServices;
    @Autowired
    private TransactionHistoryServices transactionHistoryServices;
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
    public APIResponse confirmRent(String idRents) {
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
    public APIResponse splitTextToAddDb(VoiceRequest voiceRequest){
        //thêm Hoàng Nhân số tiền 20000 email nhoang2929@gmail.com
        voiceRequest.setText(voiceRequest.getText().toLowerCase());
        String[] fullNameParts = voiceRequest.getText().split("thêm");
        if (fullNameParts.length < 2) {
            return new APIResponse(404, "full name not found", null);
        }
        String fullName = fullNameParts[1].split("số tiền")[0].trim();
        String[] moneyParts = voiceRequest.getText().split("số tiền");
        if (moneyParts.length < 2){
            return new APIResponse(404, "money not found", null);
        }
        String moneyString = moneyParts[1].split("email")[0].trim();
        moneyString = moneyString.replace(".", "").replace(",", "").replaceAll("[^0-9]", "");
        float money = Float.parseFloat(moneyString);
        String[] emailParts = voiceRequest.getText().split("email");
        if (emailParts.length < 2){
            return new APIResponse(404, "email not found", null);
        }
        String email = emailParts[1].trim();
        if (fullName.isEmpty() || email.isEmpty()){
            return new APIResponse(404, "full name or email not found", null);
        }
        Account account = accountService.getAccount(voiceRequest.getIdAccount());
        if (account == null) {
            return new APIResponse(404, "account not found", null);
        }
        Users users = new Users();
        users.setNameUser(fullName);
        users.setEmail(email);
        users.setMoney(money);
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7));
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        users.setDateLend(localDateTime);
        users.setAccount(account);
        userRepository.save(users);
        emailServices.sendEmailConfirm(mailSender,users,account);
        return new APIResponse(200, "success", "Đã lưu lại thông tin thành công");
    }
    public String vnpayHandle(HttpServletRequest req, Users users) {
        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
        long amount = (long) (users.getMoney() * 100);
        String vnp_IpAddr = VnPayConfig.getIpAddress(req);
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", users.getId());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }
    public APIResponse statusPayment(String vnp_ResponseCode, String infoPayment){
        if (vnp_ResponseCode.equals("00")){
            Users user = userRepository.findById(infoPayment).get();
            String nameUser = user.getNameUser();
            Account account = user.getAccount();
            account.setWallet(account.getWallet() + user.getMoney());
            TrainsactionHistory trainsactionHistory = new TrainsactionHistory();
            trainsactionHistory.setNameUser(nameUser);
            trainsactionHistory.setBalance(user.getMoney());
            trainsactionHistory.setAccount(account);
            accountService.updateWallet(account);
            transactionHistoryServices.addHistory(trainsactionHistory);
            emailServices.sendEmailPayment(mailSender,account,user);
            emailServices.sendPaymentEmail(user,mailSender,user.getMoney());
            userRepository.delete(user);
            return new APIResponse(200, "success", "thanh toán thành công");
        }
        return new APIResponse(400, "failed", vnp_ResponseCode);
    }
}
