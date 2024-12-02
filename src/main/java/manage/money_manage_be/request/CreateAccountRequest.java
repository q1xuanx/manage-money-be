package manage.money_manage_be.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Thông tin để đăng ký tài khoản mới")
public class CreateAccountRequest {
    @ApiModelProperty(value = "Tên đăng nhập của người dùng", example = "john_doe")
    private String userName;
    @ApiModelProperty(value = "Mật khẩu của người dùng", example = "password123")
    private String password;
    @ApiModelProperty(value = "Tên đầy đủ của người dùng", example = "John Doe")
    private String fullName;
    @ApiModelProperty(value = "Email của người dùng", example = "john.doe@example.com")
    private String email;
}
