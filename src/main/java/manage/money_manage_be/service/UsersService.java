package manage.money_manage_be.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    public APIResponse createUser(Users user) throws MessagingException {
        if (user.getNameUser().isEmpty()) {
            return new APIResponse(404, "name is empty", null);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        user.setDateLend(localDateTime);
        userRepository.save(user);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(user.getEmail());
        helper.setSubject("BẠN ĐÃ MƯỢN TIỀN THÀNH CÔNG");
        String body = "<h2>Bạn đã mượn tiền thành công!</h2>" +
                "<p>Chào bạn,</p>" +
                "<p>Bạn đã mượn Hoàng Nhân số tiền: <strong>" + user.getMoney() + "</strong> vào ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                "<p>Trân trọng,<br>Pham Hoang Nhan</p>";
        helper.setText(body, true);
        helper.setFrom("nhanphmhoang@gmail.com");
        mailSender.send(message);
        return new APIResponse(200, "saved", user);
    }
    public APIResponse getALlUsers(){
        List<Users> users = userRepository.findAll();
        return new APIResponse(200, "list users", users);
    }
    public APIResponse totalDay(){
        List<Users> users = userRepository.findAll();
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
            String body = "<h2>Bạn còn nhớ hay đã quên</h2>" +
                    "<p>Chào bạn,</p>" +
                    "<p>Bạn còn nợ Hoàng Nhân: <strong>" + user.getMoney() + "</strong> vào ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                    "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                    "<p>Trân trọng,<br>Pham Hoang Nhan</p>";
            helper.setText(body,true);
            helper.setSubject("[NHẮC NHỞ] Bạn còn nhớ không?");
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
            if (user.getMoney() <= 0) {
                userRepository.delete(user);
            }else {
                userRepository.save(user);
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(user.getEmail());
            helper.setSubject("BẠN ĐÃ TRẢ TIỀN THÀNH CÔNG");
            String body = "<h2>Bạn đã trả tiền thành công!</h2>" +
                    "<p>Chào bạn,</p>" +
                    "<p>Bạn đã trả Hoàng Nhân số tiền: <strong>" + money + "</strong> vào ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                    "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                    "<p>Trân trọng,<br>Pham Hoang Nhan</p>";
            helper.setText(body, true);
            helper.setFrom("nhanphmhoang@gmail.com");
            mailSender.send(message);
            return new APIResponse(200,"success", "ok da tru " + money);
        }
        return new APIResponse(404, "not found", null);
    }
}
