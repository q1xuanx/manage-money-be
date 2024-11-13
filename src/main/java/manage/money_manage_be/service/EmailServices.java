package manage.money_manage_be.service;

import jakarta.mail.internet.MimeMessage;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.models.Users;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class EmailServices {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    public void sendEmailConfirm(JavaMailSender mailSender, Users users, Account account) {
        CompletableFuture.runAsync(() -> {
            try{
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setTo(users.getEmail());
                String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                        "<h2 style='color: #4CAF50; text-align: center;'>Bạn đã mượn tiền thành công!</h2>" +
                        "<p>Chào bạn,</p>" +
                        "<p>Bạn đã mượn từ <strong>" + account.getFullName() + "</strong> số tiền: <strong style='color: #FF5722;'>" + users.getMoney() + " VND</strong> vào ngày: <strong>" + users.getDateLend() + "</strong>.</p>" +
                        "<p>Vui lòng nhấp vào liên kết dưới đây để xác nhận:</p>" +
                        "<p style='text-align: center;'><a href='https://roaring-pudding-f3daf4.netlify.app/confirm.html?id=" + users.getId() + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Xác nhận ngay</a></p>" +
                        "<p style='margin-top: 20px;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
                        "<p style='margin-top: 40px; text-align: right;'>Trân trọng,<br><strong>" + account.getFullName() +"</strong></p>" +
                        "<hr style='border: 0; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                        "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không trả lời.</p>" +
                        "</div>";
                helper.setText(body, true);
                helper.setFrom("nhanphmhoang@gmail.com");
                helper.setSubject("no-reply");
                mailSender.send(message);
            }catch (Exception e){
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
