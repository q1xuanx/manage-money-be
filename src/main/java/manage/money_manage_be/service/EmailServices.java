package manage.money_manage_be.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.Users;
import manage.money_manage_be.request.InfoUserRentRequest;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
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
                mailSender.send(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void thanksEmail(String email, JavaMailSender mailSender){
        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setTo(email);
                helper.setSubject("Thanks Letter");
                String body = "<html>"
                        + "<head>"
                        + "<style>"
                        + "  body { font-family: 'Arial', sans-serif; background-color: #0a0a0a; margin: 0; padding: 0; }"
                        + "  .container { max-width: 600px; margin: 20px auto; padding: 20px; background-color: #1a1a1a; border-radius: 10px; box-shadow: 0 0 20px rgba(255, 215, 0, 0.5); }"
                        + "  h2 { color: #ffd700; text-align: center; font-size: 28px; margin-bottom: 20px; }"
                        + "  p { color: #ffffff; line-height: 1.6; font-size: 16px; }"
                        + "  .footer { margin-top: 20px; padding-top: 10px; border-top: 1px solid #ffd700; text-align: center; color: #ffd700; }"
                        + "  .new-year-text { color: #ffd700; font-weight: bold; text-align: center; font-size: 24px; margin-top: 20px; }"
                        + "  .fireworks { text-align: center; margin-top: 20px; }"
                        + "  .fireworks span { display: inline-block; width: 10px; height: 10px; background-color: #ffd700; border-radius: 50%; margin: 0 5px; animation: fireworks 1.5s infinite; }"
                        + "  @keyframes fireworks {"
                        + "    0% { transform: translateY(0) scale(1); opacity: 1; }"
                        + "    100% { transform: translateY(-20px) scale(0.5); opacity: 0; }"
                        + "  }"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<div class='container'>"
                        + "  <h2>Kính gửi Quý Khách,</h2>"
                        + "  <p>Cảm ơn bạn đã tin tưởng và sử dụng ứng dụng mượn tiền trong suốt thời gian qua, năm mới chúc bạn thành công trong công việc và cuộc sống !</p>"
                        + "  <div class='new-year-text'>Happy New Year @ 2025 - Pay Remind</div>"
                        + "  <div class='fireworks'>"
                        + "    <span></span>"
                        + "    <span></span>"
                        + "    <span></span>"
                        + "    <span></span>"
                        + "    <span></span>"
                        + "  </div>"
                        + "  <div class='footer'>"
                        + "    <p>Trân trọng,</p>"
                        + "    <p><strong>Pay Remind</strong></p>"
                        + "  </div>"
                        + "</div>"
                        + "</body>"
                        + "</html>";
                helper.setText(body, true);
                mailSender.send(message);
                System.out.println("Send success");
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void sendBackEmailToAccount(Account account, JavaMailSender mailSender, Set<InfoUserRentRequest> list) {
        CompletableFuture.runAsync(() -> {
            try {
                String body = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>
                    <h2 style='color: #4CAF50; text-align: center;'>Danh sách người dùng chưa xác nhận khoản vay</h2>
                    <p>Chào bạn,</p>
                    <p>Dưới đây là danh sách các người dùng chưa xác nhận:</p>
                    <table style='width: 100%%; border-collapse: collapse; margin-top: 20px;'>
                        <thead>
                            <tr style='background-color: #0288d1; color: white;'>
                                <th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Tên</th>
                                <th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Số tiền (VNĐ)</th>
                                <th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    <p style='margin-top: 20px;'>Vui lòng kiểm tra và liên hệ lại nếu có bất kỳ thắc mắc nào.</p>
                    <p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>%s</strong></p>
                    <hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>
                    <p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>
                </div>
                """;
                StringBuilder tableRows = new StringBuilder();
                for (InfoUserRentRequest user : list) {
                    tableRows.append(String.format(
                            "<tr style='border: 1px solid #ddd;'>"
                                    + "<td style='padding: 12px; border: 1px solid #ddd;'>%s</td>"
                                    + "<td style='padding: 12px; border: 1px solid #ddd; text-align: right;'>%,.0f</td>"
                                    + "<td style='padding: 12px; border: 1px solid #ddd;'>%s</td>"
                                    + "</tr>",
                            user.getNameUser(),
                            user.getMoney(),
                            user.getEmailUser()
                    ));
                }
                String emailContent = body.formatted(tableRows.toString(), account.getFullName());
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(account.getEmail());
                helper.setSubject("Thông tin người dùng chưa xác nhận khoản vay");
                helper.setText(emailContent, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
