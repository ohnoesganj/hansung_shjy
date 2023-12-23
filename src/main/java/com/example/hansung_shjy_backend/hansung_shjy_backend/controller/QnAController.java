package com.example.hansung_shjy_backend.hansung_shjy_backend.controller;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.QnADTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Couple;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.QnA;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.User;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.CoupleRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.QnARepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.UserRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.QnAService;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.UserService;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class QnAController {

    @Autowired
    private QnAService qnAService;

    @Autowired
    private UserService userService;

    @Autowired
    private QnARepository qnARepository;

    @Autowired
    private CoupleRepository coupleRepository;


    // 오늘의 질문 첫 화면 ==========================================================
    @GetMapping("/qna")
    public ResponseEntity<Object> firstQnA(@RequestParam Integer couple_id) throws ExecutionException, InterruptedException {
        System.out.println("qna coupleid:: " + couple_id);
        List<QnA> qnAList = qnAService.listQnA(couple_id);
        System.out.println("qnAList:: " + qnAList);
        return ResponseEntity.ok().body(qnAList);
    }

    // 오늘의 질문 세부화면 =========================================================
    @GetMapping("/qna/detail/{qna_id}")
    public ResponseEntity<Object> detailQnA(@PathVariable Integer qna_id, @RequestParam Integer couple_id) throws ExecutionException, InterruptedException {
        System.out.println("qnaDetail qnaID:: " + qna_id);
        System.out.println("qnaDetail coupleid:: " + couple_id);
        QnA qnA = qnAService.detailQnA(qna_id);  // qna detail info
        User me = coupleRepository.findByCoupleID(couple_id).getMe();
        String myNickname = me.getNickname();
        String otherNickname = me.getOtherID();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("qnaDetail", qnA);
        resultMap.put("myNickname", myNickname);
        resultMap.put("otherNickname", otherNickname);

        return ResponseEntity.ok().body(resultMap);
    }

    // 오늘의 질문 저장 ============================================================
    @PostMapping("/qna/save")
    public ResponseEntity<Object> saveQnA(@RequestBody QnADTO qnADTO) throws ExecutionException, InterruptedException {
        System.out.println("save QnA:: " + qnADTO);

        User currentUser = getCurrentUser();

        Couple couple = coupleRepository.findByMeOrOther(currentUser, currentUser);
        QnA qna = qnAService.saveQnA(qnADTO, couple);

        System.out.println("myAnswer:: " + qna.getMyAnswer());
        System.out.println("otherAnswer:: " + qna.getOtherAnswer());

        if (currentUser.equals(couple.getMe())) {
            qna.setMyAnswer(qnADTO.getMyAnswer());
        } else if (currentUser.equals(couple.getOther())) {
            qna.setOtherAnswer(qnADTO.getOtherAnswer());
        }

        qnARepository.save(qna);

        if (qna == null) return new ResponseEntity<Object>("null exception", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(qna, HttpStatus.CREATED);
    }

    private User getCurrentUser() { // 이게 아닌 거 같은데 .. 일단 해봐
        return getCurrentUser();
    }


    // 오늘의 질문 수정 ============================================================
    @PatchMapping("/qna/edit/{qna_id}")
    public ResponseEntity<Object> editQnA(@PathVariable Integer qna_id, @RequestBody QnADTO qnADTO) throws ExecutionException, InterruptedException {
        System.out.println("qnaEdit qna_id:: " + qna_id);
        System.out.println("qnaEdit qnaDTO:: " + qnADTO);

        QnADTO qna = qnAService.modifyQnA(qnADTO);

        if (qna == null) return new ResponseEntity<>("null exception", HttpStatus.BAD_REQUEST);
        else return ResponseEntity.ok().body(qna);
    }
}
