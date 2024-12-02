package manage.money_manage_be.models;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ApiModel(description = "Thông tin của account sử dụng website")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ApiModelProperty(value = "ID tài khoản", example = "1a4ea230-35b4-4601-b22d-351f70e739e0")
    private String idAccount;
    @ApiModelProperty(value = "Tên tài khoản", example = "admin")
    private String username;
    @ApiModelProperty(value = "Mật khẩu", example = "admin")
    private String password;
    @ApiModelProperty(value = "Tên đầy đủ của user", example = "Phạm Hoàng Nhân")
    private String fullName;
    @ApiModelProperty(value = "Email của user", example = "nhoang2929@gmail.com")
    private String email;
    @ApiModelProperty(value = "Trạng thái của tài khoản 0 là chưa confirm và 1 là confirm", example = "0")
    @Column(columnDefinition = "integer default 0")
    private int isConfirm;
    @ApiModelProperty(value = "Thời gian xác nhận (Không cần thêm dữ liệu này ở FE)", example = "0")
    private LocalDateTime dateSendConfirm;
    @ColumnDefault("0.0")
    @ApiModelProperty(value = "Số tiền trong ví của user", example = "0")
    private double wallet;
}
