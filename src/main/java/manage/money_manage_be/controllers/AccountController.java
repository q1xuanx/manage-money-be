package manage.money_manage_be.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.CreateAccountRequest;
import manage.money_manage_be.request.LoginRequest;
import manage.money_manage_be.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "API quản lý tài khoản trong hệ thống", description = "API để quản lý tài khoản")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Endpoint để đăng nhập vào hệ thống bằng username và password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 200, \"message\": \"jwt toke\", \"data\": \"id_account\" }"))),
            @ApiResponse(responseCode = "400", description = "Username hoặc password không đúng", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 400, \"message\": \"username or password incorrect\", \"data\": null }")))
    })
    public APIResponse loginController(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin đăng nhập",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"username\": \"john_doe\", \"password\": \"password123\" }"
                            )
                    )
            ) LoginRequest loginRequest) {
        return accountService.login(loginRequest);
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký", description = "Endpoint để đăng ký tài khoản mới trong hệ thống.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 200, \"message\": \"success please confirm your email\", \"data\": { \"userName\": \"john_doe\", \"email\": \"john.doe@example.com\" } }"))),
            @ApiResponse(responseCode = "400", description = "Username đã tồn tại", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 400, \"message\": \"username exist || email exist\", \"data\": null }")))
    })
    public APIResponse registerController(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin tài khoản",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateAccountRequest.class),
                            examples = @ExampleObject(
                                    value = "{ \"userName\": \"john_doe\", \"password\": \"password123\", \"fullName\": \"John Doe\", \"email\": \"john.doe@example.com\" }"
                            )
                    )
            ) CreateAccountRequest accountRequest) {
        return accountService.register(accountRequest);
    }

    @GetMapping("/get-account/{idAccount}")
    @Operation(summary = "Lấy thông tin tài khoản", description = "Endpoint để lấy thông tin của tài khoản dựa trên ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin tài khoản thành công", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 200, \"message\": \"success\", \"data\": { \"idAccount\": \"12345\", \"fullName\": \"John Doe\", \"email\": \"john.doe@example.com\", \"wallet\": 100.0 } }"))),
            @ApiResponse(responseCode = "404", description = "Tài khoản không được tìm thấy", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 404, \"message\": \"account does not exist or is confirmed\", \"data\": null }")))
    })
    public APIResponse getAccount(
            @PathVariable @Parameter(description = "ID tài khoản", example = "12345", required = true) String idAccount) {
        return accountService.getInformation(idAccount);
    }

    @GetMapping("/list-account")
    @Operation(summary = "Lấy danh sách tài khoản", description = "Endpoint để lấy danh sách tất cả các tài khoản.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách tài khoản thành công", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 200, \"message\": \"success\", \"data\": [ { \"idAccount\": \"12345\", \"fullName\": \"John Doe\", \"email\": \"john.doe@example.com\", \"wallet\": 100.0 }, { \"idAccount\": \"67890\", \"fullName\": \"Jane Doe\", \"email\": \"jane.doe@example.com\", \"wallet\": 150.0 } ] }")))
    })
    public APIResponse listAccount() {
        return accountService.listAccounts();
    }

    @GetMapping("/confirm/{idAccount}")
    @Operation(summary = "Xác nhận email của người dùng", description = "Endpoint để xác nhận email của người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xác nhận thành công", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 200, \"message\": \"confirm\", \"data\": null }"))),
            @ApiResponse(responseCode = "400", description = "Tài khoản không tồn tại hoặc đã được xác nhận", content = @Content(schema = @Schema(implementation = APIResponse.class), examples = @ExampleObject(value = "{ \"code\": 400, \"message\": \"account does not exist or is confirmed\", \"data\": null }")))
    })
    public ResponseEntity<String> confirmAccount(
            @PathVariable @Parameter(description = "ID tài khoản", example = "12345", required = true) String idAccount) {
        return accountService.confirmEmail(idAccount);
    }
}
