package manage.money_manage_be.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.request.CreateNewUserRequest;
import manage.money_manage_be.request.VoiceRequest;
import manage.money_manage_be.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Api(value = "API quản lý user mượn tiền trong hệ thống")
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/create")
    @ApiOperation(value = "Tạo mới user", notes = "Endpoint để tạo một user mới với thông tin như tên, email, số tiền và tài khoản.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tạo mới thành công"),
            @ApiResponse(code = 404, message = "Tên hoặc tài khoản không được tìm thấy")
    })
    public APIResponse createNew(@RequestBody CreateNewUserRequest users) throws MessagingException {
        return usersService.createUser(users);
    }

    @GetMapping("/{idUser}")
    @ApiOperation(value = "Lấy danh sách user", notes = "Endpoint để lấy danh sách các user thuộc một tài khoản cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lấy danh sách user thành công"),
            @ApiResponse(code = 404, message = "Tài khoản không được tìm thấy")
    })
    public APIResponse getUsers(@PathVariable String idUser) {
        return usersService.getALlUsers(idUser);
    }

    @PatchMapping("/update/{nameUser}/{total}")
    @ApiOperation(value = "Cập nhật số tiền cho user", notes = "Endpoint để cập nhật số tiền cho một user dựa trên tên user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cập nhật thành công"),
            @ApiResponse(code = 404, message = "User không được tìm thấy")
    })
    public APIResponse updateUser(@PathVariable String nameUser, @PathVariable Float total) throws MessagingException {
        return usersService.deleteMoneyLend(total, nameUser);
    }

    @GetMapping("/totals/{idUser}")
    @ApiOperation(value = "Lấy tổng số tiền trong ngày", notes = "Endpoint để lấy tổng số tiền của tất cả các user trong một tài khoản cụ thể trong ngày.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lấy tổng số tiền thành công"),
            @ApiResponse(code = 404, message = "Tài khoản không được tìm thấy")
    })
    public APIResponse getTotals(@PathVariable String idUser) {
        return usersService.totalDay(idUser);
    }

    @GetMapping("/remind/{idUser}")
    @ApiOperation(value = "Gửi email nhắc nhở", notes = "Endpoint để gửi email nhắc nhở cho một user cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email nhắc nhở đã được gửi"),
            @ApiResponse(code = 404, message = "User không được tìm thấy")
    })
    public APIResponse remind(@PathVariable String idUser) throws MessagingException {
        return usersService.remind(idUser);
    }

    @PostMapping("/confirm/{id}")
    @ApiOperation(value = "Xác nhận giao dịch", notes = "Endpoint để xác nhận giao dịch cho một user cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Giao dịch đã được xác nhận"),
            @ApiResponse(code = 404, message = "User không được tìm thấy hoặc đã xác nhận trước đó")
    })
    public APIResponse confirm(@PathVariable String id) throws MessagingException {
        return usersService.confirmRent(id);
    }

    @GetMapping("/analyst/{id}")
    @ApiOperation(value = "Phân tích số liệu user đã thuê", notes = "Endpoint để phân tích tổng số tiền của các user đã thuê trong một tài khoản cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Phân tích thành công"),
            @ApiResponse(code = 404, message = "Tài khoản không được tìm thấy")
    })
    public APIResponse analyst(@PathVariable String id) {
        return usersService.totalOfUserHaveRent(id);
    }

    @PostMapping("/voice")
    @ApiOperation(value = "Xử lý lệnh bằng giọng nói", notes = "Endpoint để xử lý lệnh bằng giọng nói và tạo mới user từ lệnh này.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Xử lý thành công"),
            @ApiResponse(code = 404, message = "Thông tin không đầy đủ hoặc tài khoản không được tìm thấy")
    })
    public APIResponse voice(@RequestBody VoiceRequest voiceRequest) {
        return usersService.splitTextToAddDb(voiceRequest);
    }
}
