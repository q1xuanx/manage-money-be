package manage.money_manage_be.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.Users;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class EmailServices {
    private static final String BASE_URL = "http://52.200.132.159";
    @Autowired
    private UsersService usersService;
    @Autowired
    private HttpServletRequest request;
    public void sendEmailConfirm(JavaMailSender mailSender, Users users, Account account) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                String payUrl = usersService.vnpayHandle(request, users);
                helper.setTo(users.getEmail());
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #4CAF50; text-align: center;'>Bạn đã mượn tiền thành công!</h2>" +
                        "<p>Chào bạn,</p>" +
                        "<p>Bạn đã mượn từ <strong>" + account.getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + users.getMoney() + " VND</strong> vào ngày: <strong>" + users.getDateLend() + "</strong>.</p>" +
                        "<p>Vui lòng nhấp vào liên kết dưới đây để xác nhận:</p>" +
                        "<p style='text-align: center;'><a href='"+BASE_URL+"/confirm/"+users.getId()+"' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Xác nhận ngay</a></p>" +
                        "<p style='text-align: center;'><a href='"+payUrl+"' style='display: inline-block; background-color: #FF5722; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Thanh toán ngay</a></p>" +
                        "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                        "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>" + account.getFullName() + "</strong></p>" +
                        "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                        "</div>";
                helper.setText(body, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setSubject("no-reply");
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void sendEmailPayment(JavaMailSender mailSender, Account account, Users user) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(account.getEmail());
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #4CAF50; text-align: center;'>Thông báo: Đã nhận thanh toán</h2>" +
                        "<p>Chào bạn,</p>" +
                        "<p>Bạn đã nhận được khoản thanh toán từ <strong>" + user.getNameUser() + "</strong>.</p>" +
                        "<p>Số tiền: <strong style='color: #FF5722;'>" + user.getMoney() + " VND</strong>.</p>" +
                        "<p>Ngày thanh toán: <strong>" + now + "</strong>.</p>" +
                        "<p>Vui lòng kiểm tra ví của bạn để xác nhận.</p>" +
                        "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                        "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>Đội ngũ hỗ trợ</strong></p>" +
                        "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                        "</div>";
                helper.setText(body, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setSubject("Thông báo: Đã nhận thanh toán");
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void confirmAccount(JavaMailSender mailSender, Account account) {
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(account.getEmail());
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #4CAF50; text-align: center;'>Xác nhận tài khoản của bạn</h2>" +
                        "<p>Chào bạn,</p>" +
                        "<p>Chúng tôi đã nhận được yêu cầu xác nhận tài khoản từ bạn.</p>" +
                        "<p>Vui lòng nhấp vào liên kết dưới đây để xác nhận tài khoản của bạn:</p>" +
                        "<p style='text-align: center;'><a href='"+BASE_URL+"/auth/confirm/" + account.getIdAccount() + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Xác nhận ngay</a></p>" +
                        "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                        "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>Đội ngũ hỗ trợ</strong></p>" +
                        "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                        "</div>";
                helper.setText(body, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setSubject("Xác nhận tài khoản của bạn");
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void sendRemindEmail(JavaMailSender mailSender, Users user){
        CompletableFuture.runAsync(() -> {
            try{
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setTo(user.getEmail());
                String payUrl = usersService.vnpayHandle(request,user);
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #FF5722; text-align: center;'>Bạn còn nhớ hay đã quên</h2>" +
                        "<p>Chào bạn,</p>" +
                        "<p>Bạn còn nợ <strong>" + user.getAccount().getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + user.getMoney() + " VND</strong> từ ngày: <strong>" + user.getDateLend() + "</strong>.</p>" +
                        "<p style='text-align: center;'><a href='"+payUrl+"' style='display: inline-block; background-color: #FF5722; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Thanh toán ngay</a></p>" +
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
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void sendEmailSuccessRent(JavaMailSender mailSender, Users user){
        CompletableFuture.runAsync(() -> {
            try{
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(user.getEmail());
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" + "<h2 style='color: #4CAF50; text-align: center;'>Bạn đã mượn tiền thành công!</h2>" + "<p>Chào bạn,</p>" + "<p>Bạn đã mượn từ <strong>" + user.getAccount().getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + user.getMoney() + " VND</strong> vào ngày: <strong>" + user.getDateLend() + "</strong>.</p>" + "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" + "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>" + user.getAccount().getFullName() +"</strong></p>" + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" + "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" + "</div>";
                helper.setText(body, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                mailSender.send(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void sendPaymentEmail(Users user, JavaMailSender mailSender, float money){
        CompletableFuture.runAsync(() -> {
            try {
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
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

}
