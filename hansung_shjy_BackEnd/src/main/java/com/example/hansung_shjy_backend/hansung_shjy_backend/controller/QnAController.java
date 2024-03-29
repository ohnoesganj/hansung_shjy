package com.example.hansung_shjy_backend.hansung_shjy_backend.controller;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.QnADTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.QnARequest;
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
    private QnARepository qnARepository;

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private UserRepository userRepository;


    // 오늘의 질문 첫 화면 ==========================================================
    @GetMapping("/qna")
    public ResponseEntity<Object> firstQnA(@RequestParam("coupleID") Integer couple_id) throws ExecutionException, InterruptedException {
        System.out.println("qna coupleid:: " + couple_id);
        List<QnA> qnAList = qnAService.listQnA(couple_id);
        System.out.println("qnAList:: " + qnAList);

        Couple couple = coupleRepository.findByCoupleID(couple_id);
        Integer me = couple.getMe().getUserID();

        HashMap<String, Object> map = new HashMap<>();
        map.put("qnAList", qnAList);
        map.put("me", me);

        return ResponseEntity.ok().body(map);
    }

    // 오늘의 질문 세부화면 =========================================================
    @GetMapping("/qna/detail/{qna_number}")
    public ResponseEntity<Object> detailQnA(@PathVariable Integer qna_number, @RequestParam("coupleID") Integer couple_id) throws ExecutionException, InterruptedException {
        System.out.println("qnaDetail qnaID:: " + qna_number);
        System.out.println("qnaDetail coupleid:: " + couple_id);
        QnA qnA = qnAService.detailQnA(qna_number);  // qna detail info
        User me = coupleRepository.findByCoupleID(couple_id).getMe();
        String myNickname = me.getNickname();
        String otherNickname = me.getOtherID();
        Integer coupleID = me.getCouple().getCoupleID();
        Couple couple = coupleRepository.findByCoupleID(coupleID);
        Integer myUserID = couple.getMe().getUserID();
        Integer otherUserID = couple.getOther().getUserID();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("qnaDetail", qnA);
        resultMap.put("nickname1", myNickname);
        resultMap.put("nickname2", otherNickname);
        resultMap.put("userID1", myUserID);
        resultMap.put("userID2", otherUserID);

        return ResponseEntity.ok().body(resultMap);
    }

    // 오늘의 질문 저장 ============================================================
    @PostMapping("/qna/save")
    public ResponseEntity<Object> saveQnA(@RequestBody QnARequest qnARequest) throws ExecutionException, InterruptedException {
        System.out.println("save QNANANANA:: " + qnARequest.getMyAnswer() +
                qnARequest.getOtherAnswer() + qnARequest.getQnaNumber());
        // qnA -> Date, myAnswer, otherAnswer, userID, coupleID

        Couple couple = coupleRepository.findByCoupleID(qnARequest.getCoupleID());
        User me = userRepository.findUserByUserID(qnARequest.getUserID());

        QnA qna = qnAService.saveQnA(qnARequest, couple);
        QnA existingQnA = qnARepository.findByCoupleIDAndQnaNumber(couple, qnARequest.getQnaNumber());
        System.out.println("QnACouple:: " + couple);

        if (existingQnA == null) {
            if (me.getUserID().equals(couple.getMe().getUserID())) {  // 나 -> couple의 me면
                qna.setMyAnswer(qnARequest.getMyAnswer());
            } else {                                                  // 나 -> couple의 other면
                qna.setOtherAnswer(qnARequest.getMyAnswer());
            }

            qnARepository.save(qna);
        } else {
            if (me.getUserID().equals(couple.getOther().getUserID())) {  // 나 -> couple의 other면
                existingQnA.setOtherAnswer(qnARequest.getMyAnswer());
            } else {                                                     // 나 -> couple의 me면
                existingQnA.setMyAnswer(qnARequest.getMyAnswer());
            }
            System.out.println("otherAnswer:: " + qnARequest.getOtherAnswer());

            qnARepository.save(existingQnA);
        }

        if (qna == null) return new ResponseEntity<Object>("null exception", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(qna, HttpStatus.CREATED);
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
