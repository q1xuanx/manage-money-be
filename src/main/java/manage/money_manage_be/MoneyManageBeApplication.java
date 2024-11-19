package manage.money_manage_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyManageBeApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoneyManageBeApplication.class, args);
	}

}
