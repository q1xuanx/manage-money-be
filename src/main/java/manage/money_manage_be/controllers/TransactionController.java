package manage.money_manage_be.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import manage.money_manage_be.reponse.APIResponse;
import manage.money_manage_be.service.TransactionHistoryServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Tag(name = "API lấy lịch sử giao dịch", description = "API lấy lịch sử giao dịch cua từng account")
public class TransactionController {
    @Autowired
    private TransactionHistoryServices transactionHistoryServices;

    @GetMapping("/transaction/{idAccount}")
    @Operation(summary = "Lấy lịch sử trả tiền", description = "Lấy lịch sử mượn tiền theo id của tài khoản")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy lịch sử trả tiền thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    value = "{ \"code\": 200, \"message\": \"get success\", \"data\": [ { \"idTransaction\": 1, \"namePay\": \"hoang nhan\", \"account\": \"e768246b-3fbb-42f2-acd0-cc9a77048850\", \"amount\": 5000.0, \"date\": \"2024-12-02T14:17:35.705531\" } ] }"
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
    public APIResponse getTransactionHistory(
            @PathVariable @Parameter(name = "idAccount", description = "Id của tài khoản đang đăng nhập", example = "e768246b-3fbb-42f2-acd0-cc9a77048850", required = true)
            String idAccount) {
        return transactionHistoryServices.getHistoryOfUser(idAccount);
    }

}
