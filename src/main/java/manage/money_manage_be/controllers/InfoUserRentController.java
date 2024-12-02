package manage.money_manage_be.controllers;


import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.CreateNewUserRequest;
import manage.money_manage_be.request.VoiceRequest;
import manage.money_manage_be.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "API quản lý những nguời nợ trong hệ thong", description = "API quản lý những nguời nợ trong hệ thong")
public class InfoUserRentController {
    private final UsersService usersService;

    @PostMapping("/create")
    @Operation(summary = "Tạo mới người mượn tiền", description = "Endpoint để tạo một người mượn tiền mới với các thông tin như tên, số tiền, email và ID tài khoản.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tạo người mượn tiền thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"saved\", \"data\": { \"nameUser\": \"Phạm Hoàng Nhan\", \"money\": 10000, \"email\": \"nhoang2929@gmail.com\", \"idAccount\": \"id của tài khoản đã đăng nhập\" } }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tên của người mượn trống hoặc email bị bỏ trống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 400, \"message\": \"name is empty or email is empty\", \"data\": null }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tài khoản đăng nhập không tồn tại",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 404, \"message\": \"account not found\", \"data\": null }"
                            )
                    )
            )
    })
    public APIResponse createNewRent(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin người mượn tiền",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateNewUserRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"nameUser\": \"Phạm Hoàng Nhan\", \"money\": 10000, \"email\": \"nhoang2929@gmail.com\", \"idAccount\": \"id của tài khoản đã đăng nhập\" }"
                            )
                    )
            ) CreateNewUserRequest users) {
        return usersService.createUser(users);
    }


    @GetMapping("/get-list-info-rent/{idAccount}")
    @Operation(summary = "Lấy danh sách user", description = "Endpoint để lấy danh sách các user thuộc tài khoản cụ thể dựa trên idAccount.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách user thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"list users\", \"data\": [ { \"id\": \"839f9465-acd1-4c28-ae99-c31f5423cdcc\", \"nameUser\": \"hoang nhan\", \"money\": 10000.0, \"email\": \"nhoang2929@gmail.com\", \"dateLend\": \"2024-12-02T14:17:35.705531\", \"account\": { \"idAccount\": \"c5543f59-fddf-4fce-b63b-1a1af89e4931\", \"username\": \"nhan12348\", \"password\": \"$2a$10$23pfKD1zfmCX59DnZcAiqu7ebeIcO1jXzypBcGLl9tuYC48By/lSK\", \"fullName\": \"nhan\", \"email\": \"iamtest001122@gmail.com\", \"isConfirm\": 1, \"dateSendConfirm\": \"2024-12-02T12:59:33.554523\", \"wallet\": 0.0 }, \"isConfirmed\": 1 } ] }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 404, \"message\": \"account not found\", \"data\": null }"
                            )
                    )
            )
    })
    public APIResponse getListUserRent(
            @PathVariable @Parameter(name = "idAccount", description = "Id của tài khoản đang đăng nhập", example = "e768246b-3fbb-42f2-acd0-cc9a77048850", required = true)
            String idAccount) {
        return usersService.getALlUsers(idAccount);
    }


    @PatchMapping("/update/{idUser}/{total}")
    @Operation(summary = "Cập nhật số tiền đã trả cho người mượn", description = "Endpoint để cập nhật số tiền cho một user dựa trên tên id của người mượn.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"success\", \"data\": \"ok da tru 10000\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Số tiền nhập không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 400, \"message\": \"error\", \"data\": \"Số tiề nhập không hợp lệ\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 404, \"message\": \"not found\", \"data\": null }"
                            )
                    )
            )
    })
    public APIResponse updateMoneyOfUserRent(
            @PathVariable @Parameter(name = "idUser", description = "Id của người đã mượn tiền", example = "f1cdf54e-0fe3-41ba-83a4-570ac1ad421f", required = true) String idUser,
            @PathVariable @Parameter(name = "total", description = "Số tiền người đó đã trả", example = "10000", required = true) Float total) {
        return usersService.deleteMoneyLend(total, idUser);
    }

    @GetMapping("/totals/{idAccount}")
    @Operation(summary = "Tính tổng số tiền", description = "Endpoint để tính tổng số tiền mà những người đã mượn theo id của account đang đăng nhập.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tính tổng số tiền thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"total day\", \"data\": \"120000\" }"
                            )
                    )
            )
    })
    public APIResponse getTotals(
            @PathVariable @Parameter(name = "idAccount", description = "Id của tài khoản đang đăng nhập", example = "e768246b-3fbb-42f2-acd0-cc9a77048850", required = true) String idAccount) {
        return usersService.totalDay(idAccount);
    }


    @GetMapping("/remind/{idUser}")
    @Operation(summary = "Nhắc nhở người mượn tiền", description = "Endpoint để nhắc nhở qua email người đang mượn tiền.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nhắc nhở qua email thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"remind\", \"data\": [ { \"id\": \"839f9465-acd1-4c28-ae99-c31f5423cdcc\", \"nameUser\": \"hoang nhan\", \"money\": 10000.0, \"email\": \"nhoang2929@gmail.com\", \"dateLend\": \"2024-12-02T14:17:35.705531\", \"account\": { \"idAccount\": \"c5543f59-fddf-4fce-b63b-1a1af89e4931\", \"username\": \"nhan12348\", \"password\": \"$2a$10$23pfKD1zfmCX59DnZcAiqu7ebeIcO1jXzypBcGLl9tuYC48By/lSK\", \"fullName\": \"nhan\", \"email\": \"iamtest001122@gmail.com\", \"isConfirm\": 1, \"dateSendConfirm\": \"2024-12-02T12:59:33.554523\", \"wallet\": 0.0 }, \"isConfirmed\": 1 } ] }"
                            )
                    )
            )
    })
    public APIResponse remindUserRent(
            @PathVariable @Parameter(name = "idUser", description = "Id của người đang mượn tiền", example = "094db1ab-9cab-4bb9-9fc5-b10e8196fb24", required = true) String idUser) throws MessagingException {
        return usersService.remind(idUser);
    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<String> confirmRent(
            @PathVariable @Parameter(name = "id", description = "Id của người đang mượn tiền", example = "094db1ab-9cab-4bb9-9fc5-b10e8196fb24", required = true)
            String id) {
        return usersService.confirmRent(id);
    }

    @GetMapping("/analyst/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vẽ biểu đồ thống kê số tổng tiền mà những người mượn theo tên",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"remind\", \"data\": { \"van a\" : 10000, \"hoang nhan\": 10000 } }"
                            )
                    )
            )
    })
    public APIResponse analyst(
            @PathVariable @Parameter(name = "id", description = "Id của tài khoản đang đăng nhập", example = "e768246b-3fbb-42f2-acd0-cc9a77048850", required = true)
            String id) {
        return usersService.totalOfUserHaveRent(id);
    }


    @PostMapping("/voice")
    @Operation(summary = "Xử lý lệnh bằng giọng nói", description = "Endpoint để xử lý lệnh bằng giọng nói và tạo mới user từ lệnh này.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lưu thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"success\", \"data\": \"Đã lưu thông tin thành công\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Full name, số tiền, email, tài khoản không tìm thấy",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 404, \"message\": \"email or username or money or account not found\", \"data\": null }"
                            )
                    )
            )
    })
    public APIResponse voice(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin lệnh bằng giọng nói",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VoiceRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"text\": \"thêm hoàng nhân số tiền 20000 email nhoang2929@gmail.com\", \"idAccount\": \"e768246b-3fbb-42f2-acd0-cc9a77048850\" }"
                            )
                    )
            ) VoiceRequest voiceRequest) {
        return usersService.splitTextToAddDb(voiceRequest);
    }


    @GetMapping("/confirm/payment")
    public ResponseEntity<String> statusPayment(
            @RequestParam @Parameter(name = "vnp_Amount", description = "Số tiền thanh toán", required = true)
            String vnp_Amount,
            @RequestParam @Parameter(name = "vnp_BankCode", description = "Mã ngân hàng", required = true)
            String vnp_BankCode,
            @RequestParam @Parameter(name = "vnp_OrderInfo", description = "Thông tin đơn hàng", required = true)
            String vnp_OrderInfo,
            @RequestParam @Parameter(name = "vnp_ResponseCode", description = "Mã phản hồi", required = true)
            String vnp_ResponseCode,
            HttpSession httpSession) {
        return usersService.statusPayment(vnp_ResponseCode, vnp_OrderInfo);
    }

}
