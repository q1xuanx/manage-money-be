package manage.money_manage_be.scheduled;


import lombok.RequiredArgsConstructor;
import manage.money_manage_be.service.EmailServices;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ThanksLetterScheduled {
    private final EmailServices emailServices;
    private final JavaMailSender mailSender;
    @Scheduled(cron = "0 0 0 * * *")
    public void sendThanksLetter() {
        List<String> emails = new ArrayList<>();
        emails.add("minhchien03112003@gmail.com");
        emails.add("nguyenquan10k4@gmail.com");
        emails.add("gymer1707@gmail.com");
        emails.add("xthang.lib@gmail.com");
        for (String email : emails) {
            emailServices.thanksEmail(email, mailSender);
        }
    }
}
