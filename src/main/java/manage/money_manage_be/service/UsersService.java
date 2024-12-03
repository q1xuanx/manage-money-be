package manage.money_manage_be.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import manage.money_manage_be.configuation.VnPayConfig;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.TrainsactionHistory;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.reponse.ListUserResponse;
import manage.money_manage_be.reponse.UserResponse;
import manage.money_manage_be.repository.UserRepository;
import manage.money_manage_be.request.CreateNewUserRequest;
import manage.money_manage_be.request.VoiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
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
    public APIResponse createUser(CreateNewUserRequest user) {
        if (user.getNameUser().isEmpty()) {
            return new APIResponse(400, "name is empty", null);
        }
        if (user.getEmail().isEmpty()){
            return new APIResponse(400, "email is empty", null);
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
        List<UserResponse> listUsers = new ArrayList<>();
        float totals = 0;
        for (Users user : users) {
            UserResponse userResponse = new UserResponse(user.getId(), user.getNameUser(),user.getMoney(), user.getEmail(), user.getDateLend());
            listUsers.add(userResponse);
            totals += user.getMoney();
        }
        ListUserResponse listUserResponse = new ListUserResponse(listUsers, totals);
        return new APIResponse(200, "list users", listUserResponse);
    }
    public APIResponse remind(String idUser)  {
        Optional<Users> find = userRepository.findById(idUser);
        if (find.isPresent()) {
            Users user = find.get();
            emailServices.sendRemindEmail(mailSender,user);
            return new APIResponse(200, "remind", user);
        }
        return new APIResponse(404, "not found", null);
    }
    public APIResponse deleteMoneyLend (float money, String idUser) {
        List<Users> list = userRepository.findAll();
        Optional<Users> findExist = list.stream().filter(s -> s.getId().equals(idUser)).findFirst();
        if (findExist.isPresent()) {
            Users user = findExist.get();
            user.setMoney(user.getMoney() - money);
            if (money > user.getMoney()){
                return new APIResponse(400, "error", "Số tiền nhập không hợp lệ");
            }
            emailServices.sendPaymentEmail(user, mailSender, money);
            if (user.getMoney() <= 0){
                userRepository.delete(user);
                return new APIResponse(200, "success", "da tru thanh cong và xóa user do đã hết nợ");
            }
            userRepository.save(user);
            return new APIResponse(200,"success", "ok da tru " + money);
        }
        return new APIResponse(404, "not found", null);
    }
    public ResponseEntity<String> confirmRent(String idRents) {
        Optional<Users> getUser = userRepository.findById(idRents);
        if (getUser.isPresent()) {
            Users user = getUser.get();
            if (user.getIsConfirmed() == 1){
                String errorHtml = "<html>" +
                        "<head>" +
                        "<title>Thông báo lỗi</title>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f2f2f2; }" +
                        ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f44336; border-radius: 8px; background-color: #ffe6e6; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                        ".error { color: #f44336; font-size: 28px; margin-bottom: 20px; }" +
                        ".message { color: #333; font-size: 18px; }" +
                        "a { color: #f44336; text-decoration: none; }" +
                        "a:hover { text-decoration: underline; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<h2 class='error'>Bạn đã xác nhận mail này rồi</h2>" +
                        "<p class='message'>Vui lòng kiểm tra lại thông tin xác nhận của bạn.</p>" +
                        "<p class='message'><a href='mailto:nhanphamhoang@gmail.com'>Liên hệ hỗ trợ</a> nếu bạn gặp vấn đề.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
                return new ResponseEntity<>(errorHtml, HttpStatus.NOT_FOUND);
            }
            user.setIsConfirmed(1);
            userRepository.save(user);
            emailServices.sendEmailSuccessRent(mailSender,user);
            String successHtml = "<html>" +
                    "<head>" +
                    "<title>Xác nhận thành công</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f2f2f2; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #4CAF50; border-radius: 8px; background-color: #e7f9e7; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                    ".success { color: #4CAF50; font-size: 28px; margin-bottom: 20px; }" +
                    ".message { color: #333; font-size: 18px; }" +
                    "a { color: #4CAF50; text-decoration: none; }" +
                    "a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2 class='success'>Xác nhận thành công!</h2>" +
                    "<p class='message'>Cảm ơn bạn đã xác nhận. Bạn đã hoàn thành quy trình.</p>" +
                    "<p class='message'><a href='mailto:nhanphmhoang@gmail.com'>Liên hệ hỗ trợ</a> nếu bạn gặp vấn đề.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            return new ResponseEntity<>(successHtml, HttpStatus.OK);
        }
        String errorHtml = "<html>" +
                "<head>" +
                "<title>Thông báo lỗi</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f2f2f2; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f44336; border-radius: 8px; background-color: #ffe6e6; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                ".error { color: #f44336; font-size: 28px; margin-bottom: 20px; }" +
                ".message { color: #333; font-size: 18px; }" +
                "a { color: #f44336; text-decoration: none; }" +
                "a:hover { text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2 class='error'>Thông tin xác nhận không chính xác</h2>" +
                "<p class='message'>Vui lòng kiểm tra lại thông tin xác nhận của bạn.</p>" +
                "<p class='message'><a href='mailto:nhanphamhoang@gmail.com'>Liên hệ hỗ trợ</a> nếu bạn gặp vấn đề.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
        return new ResponseEntity<>(errorHtml, HttpStatus.NOT_FOUND);
    }


    // Search where user rent is not confirm after 5 min then deletes it.
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
        List<Users> list = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idUser) && s.getIsConfirmed() == 1 && s.getMoney() > 0).toList();
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
        cld.add(Calendar.MINUTE, 1);
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
    public ResponseEntity<String> statusPayment(String vnp_ResponseCode, String infoPayment) {
        if (vnp_ResponseCode.equals("00")) {
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
            emailServices.sendEmailPayment(mailSender, account, user);
            emailServices.sendPaymentEmail(user, mailSender, user.getMoney());
            userRepository.delete(user);
            String successHtml = "<html>" +
                    "<head>" +
                    "<title>Thanh toán thành công</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f2f2f2; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #4CAF50; border-radius: 8px; background-color: #e7f9e7; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                    ".success { color: #4CAF50; font-size: 28px; margin-bottom: 20px; }" +
                    ".message { color: #333; font-size: 18px; }" +
                    "a { color: #4CAF50; text-decoration: none; }" +
                    "a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2 class='success'>Thanh toán thành công!</h2>" +
                    "<p class='message'>Thanh toán thành công tới tài khoản của " + account.getFullName() + ".</p>" +
                    "<p class='message'>Cảm ơn bạn đã xác nhận. Bạn đã hoàn thành quy trình.</p>" +
                    "<p class='message'><a href='mailto:nhanphamhoang@gmail.com'>Liên hệ hỗ trợ</a> nếu bạn gặp vấn đề.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            return ResponseEntity.status(HttpStatus.OK).body(successHtml);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("<html><body><h2>Giao dịch thất bại với mã: " + vnp_ResponseCode + "</h2></body></html>");
    }

}
