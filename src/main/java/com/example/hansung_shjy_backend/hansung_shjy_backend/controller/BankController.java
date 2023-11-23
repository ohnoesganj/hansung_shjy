package com.example.hansung_shjy_backend.hansung_shjy_backend.controller;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.BankDTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Bank;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.BankService;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RestController
public class BankController {

    @Autowired
    private BankService bankService;

    @Autowired
    private UserService userService;


    // 우리의 지출 첫 화면 ===================================================================
    @GetMapping("/pay")
    public ResponseEntity<Object> firstBank(@RequestBody Integer user_id) throws ExecutionException, InterruptedException {
        System.out.println("bank userID:: " + user_id);
        List<Bank> bankList = bankService.listBank(user_id);
        System.out.println("bankList:: " + bankList);
        return ResponseEntity.ok().body(bankList);
    }

    // 우리의 지출 모달창 ====================================================================
    @GetMapping("/pay/detail/{bank_date}")
    public ResponseEntity<Object> modalBank(@PathVariable Date bank_date, @RequestBody Integer user_id) throws ExecutionException, InterruptedException {
        System.out.println("bankDate:: " + bank_date + ", " + user_id);
        List<Bank> bankList = bankService.modalBank(user_id, bank_date);
        System.out.println("bankList_modal:: " + bankList);
        return ResponseEntity.ok().body(bankList);
    }

    // 우리의 지출 등록 =====================================================================
    @PostMapping("/pay/save")
    public ResponseEntity<Object> createBank(@RequestBody BankDTO bankDTO) throws ExecutionException, InterruptedException {
        System.out.println("bankDTO:: " + bankDTO);
        BankDTO bank = bankService.createBank(bankDTO);
        System.out.println("createBank:: " + bank);
        if (bank == null) return new ResponseEntity<Object>("null exception", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(bank, HttpStatus.CREATED);
    }

    // 우리의 지출 수정 =====================================================================
    @PatchMapping("/pay/edit/{bank_id}")
    public ResponseEntity<Object> modifyBank(@PathVariable Integer bank_id, @RequestBody BankDTO bankDTO) throws ExecutionException, InterruptedException {
        System.out.println("bankmodifyId:: " + bank_id + ", " + bankDTO);
        bankDTO.setBankDate(bankDTO.getBankDate());
        bankDTO.setBankTitle(bankDTO.getBankTitle());
        bankDTO.setPayMethod(bankDTO.getPayMethod());
        bankDTO.setMoney(bankDTO.getMoney());
        bankDTO.setUserID(userService.findUserByUserid(bankDTO.getUserID()));
        BankDTO bank = bankService.modifyBank(bankDTO);
        if (bank == null) return new ResponseEntity<>("null exception", HttpStatus.BAD_REQUEST);
        else return ResponseEntity.ok().body(bank);
    }

    // 우리의 지출 삭제
    @DeleteMapping("/pay/delete/{bank_id}")
    public ResponseEntity<Object> deleteBank(@PathVariable Integer bank_id) throws ExecutionException, InterruptedException {
        System.out.println("deleteBankID:: " + bank_id);
        String delete = bankService.deleteBank(bank_id);
        if (Objects.equals(delete, "delete")) return ResponseEntity.ok().body("delete");
        else return new ResponseEntity<>("null exception", HttpStatus.BAD_REQUEST);
    }
}
