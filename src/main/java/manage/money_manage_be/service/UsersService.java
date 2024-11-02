package manage.money_manage_be.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.UserRepository;
import manage.money_manage_be.request.CreateNewUserRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final AccountService accountService;
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
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getEmail());
        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<h2 style='color: #4CAF50; text-align: center;'>Bạn đã mượn tiền thành công!</h2>" +
                "<p>Chào bạn,</p>" +
                "<p>Bạn đã mượn từ <strong>" + account.getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + users.getMoney() + " VND</strong> vào ngày: <strong>" + users.getDateLend() + "</strong>.</p>" +
                "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>Phạm Hoàng Nhân</strong></p>" +
                "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                "</div>";
        helper.setText(body, true);
        helper.setFrom("nhanphmhoang@gmail.com");
        mailSender.send(message);
        return new APIResponse(200, "saved", user);
    }
    public APIResponse getALlUsers(String idAccount){
        List<Users> users = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idAccount)).toList();
        return new APIResponse(200, "list users", users);
    }
    public APIResponse totalDay(String idAccount){
        List<Users> users = userRepository.findAll().stream().filter(s -> s.getAccount().getIdAccount().equals(idAccount)).toList();
        double total = users.stream().mapToDouble(s -> (double) s.getMoney()).sum();
        return new APIResponse(200, "total day", total);
    }
    public APIResponse remind(String idUser) throws MessagingException {
        Optional<Users> find = userRepository.findById(idUser);
        if (find.isPresent()) {
            Users user = find.get();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("nhanphmhoang@gmail.com");
            helper.setTo(user.getEmail());
            String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                    "<h2 style='color: #FF5722; text-align: center;'>Bạn còn nhớ hay đã quên</h2>" +
                    "<p>Chào bạn,</p>" +
                    "<p>Bạn còn nợ <strong>" + user.getAccount().getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + user.getMoney() + " VND</strong> từ ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                    "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                    "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>" + user.getAccount().getFullName() + "</strong></p>" +
                    "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                    "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                    "</div>";
            helper.setText(body, true);
            helper.setSubject("[NHẮC NHỞ] Bạn còn nhớ không?");
            helper.setTo(user.getEmail());
            helper.setFrom("nhanphmhoang@gmail.com");
            mailSender.send(message);
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(user.getEmail());
            helper.setSubject("BẠN ĐÃ TRẢ TIỀN THÀNH CÔNG");
            String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                    "<h2 style='color: #4CAF50; text-align: center;'>Bạn đã trả tiền thành công!</h2>" +
                    "<p>Chào bạn,</p>" +
                    "<p>Bạn đã trả cho <strong>" + user.getAccount().getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + money + " VND</strong> vào ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                    "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                    "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>" + user.getAccount().getFullName() + "</strong></p>" +
                    "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                    "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                    "</div>";
            helper.setText(body, true);
            helper.setFrom("nhanphmhoang@gmail.com");
            helper.setTo(user.getEmail());
            mailSender.send(message);
            if (user.getMoney() <= 0) {
                userRepository.delete(user);
            }else {
                userRepository.save(user);
            }
            return new APIResponse(200,"success", "ok da tru " + money);
        }
        return new APIResponse(404, "not found", null);
    }
}
