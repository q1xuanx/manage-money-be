package manage.money_manage_be.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Thông tin để tạo user mới")
public class CreateNewUserRequest {
    @ApiModelProperty(value = "Tên của user", example = "Nguyễn Văn A")
    private String nameUser;

    @ApiModelProperty(value = "Số tiền mượn", example = "100000")
    private float money;

    @ApiModelProperty(value = "Email của user", example = "nguyenvana@example.com")
    private String email;

    @ApiModelProperty(value = "ID của tài khoản", example = "abc123")
    private String idAccount;
}
