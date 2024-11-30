package manage.money_manage_be.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.models.Account;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.LoginRequest;
import manage.money_manage_be.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(value = "API quản lý tài khoản trong hệ thống")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/login")
    @ApiOperation(value = "Đăng nhập", notes = "Endpoint để đăng nhập vào hệ thống bằng username và password.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Đăng nhập thành công"),
            @ApiResponse(code = 400, message = "Username hoặc password không đúng")
    })
    public APIResponse loginController(@RequestBody LoginRequest loginRequest) {
        return accountService.login(loginRequest);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Đăng ký", notes = "Endpoint để đăng ký tài khoản mới trong hệ thống.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Đăng ký thành công"),
            @ApiResponse(code = 400, message = "Username đã tồn tại")
    })
    public APIResponse registerController(@RequestBody Account account){
        return accountService.register(account);
    }

    @GetMapping("/list-account")
    @ApiOperation(value = "Lấy danh sách tài khoản", notes = "Endpoint để lấy danh sách tất cả các tài khoản.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lấy danh sách tài khoản thành công")
    })
    public APIResponse listAccount(){
        return accountService.listAccounts();
    }
}
