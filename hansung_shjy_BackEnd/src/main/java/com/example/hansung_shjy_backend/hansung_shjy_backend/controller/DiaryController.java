package com.example.hansung_shjy_backend.hansung_shjy_backend.controller;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.DiaryEditDTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Couple;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Diary;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.User;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.CoupleRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.DiaryRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.UserRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.service.DiaryService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;


    // 홈 화면 ============================================================================
    @GetMapping("/diary")
    public ResponseEntity<Object> diaryFirst(@RequestParam("couple_id") Integer couple_id) throws ExecutionException, InterruptedException, IOException {

            List<Diary> diaryList = diaryService.listDiary(couple_id);
            List<Map<String, Object>> resultList = new ArrayList<>();

            for (Diary diary : diaryList) {
                Map<String, Object> entryMap = new HashMap<>();
                entryMap.put("diary_id", diary.getDiaryID());

                String imageName = diary.getImageName();
                String imageUrl = diary.getImageUrl();
                File imageFile = new File("/Users/project/" + imageUrl + imageName);

                if (imageFile.exists()) {
                    try (InputStream imageStream = new FileInputStream(imageFile)) {
                        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                        entryMap.put("image", imageByteArray);
                    }
                }

                resultList.add(entryMap);
            }

            return ResponseEntity.ok().body(resultList);
    }


    // 일기 세부 첫 화면 ====================================================================
    @GetMapping("/diary/detail")
    public ResponseEntity<Object> firstDetail(@RequestParam Integer couple_id) throws ExecutionException, InterruptedException {
        System.out.println("Fist Diary Detail" + couple_id);

        User me = coupleRepository.findByCoupleID(couple_id).getMe();
        String myNickname = me.getNickname();
        String otherNickname = me.getOtherID();
        Couple couple = coupleRepository.findByCoupleID(couple_id);
        Integer myUserID = me.getUserID();
        Integer otherUserID = couple.getOther().getUserID();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("nickname1", myNickname);
        resultMap.put("nickname2", otherNickname);
        resultMap.put("userID1", myUserID);
        resultMap.put("userID2", otherUserID);

        return ResponseEntity.ok().body(resultMap);
    }

    // 일기 저장 ===========================================================================
    @PostMapping("/diary/save/{couple_id}")
    public ResponseEntity<Object> createDiary(@PathVariable Integer couple_id,
                                              @RequestParam(value = "diaryDate") String diaryDate,
                                              @RequestParam(value = "myDiary") String myDiary,
                                              @RequestParam(value = "otherDiary") String otherDiary,
                                              @RequestParam(value = "userID") Integer userID,
                                              @RequestParam(value = "file", required = false) MultipartFile file) throws ExecutionException, InterruptedException {
        System.out.println("create Diary couple_id:: " + couple_id);
        System.out.println("create diaryDTO:: " + diaryDate + myDiary + userID + ", imageDTO:: " + file);

        Couple couple = coupleRepository.findByCoupleID(couple_id);  // diary 저장

        User me = userRepository.findUserByUserID(userID);

        Diary diary = diaryService.createDiary(couple, diaryDate);
        Diary existingDiary = diaryRepository.findDiaryByCoupleAndAndDiaryDate(couple_id, java.sql.Date.valueOf(diaryDate));

        // 폴더 생성과 파일명 새로 부여를 위한 현재 시간 알아내기
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);

        if (existingDiary == null) {
            MultipartFile image = file;

            String absolutePath = new File("/Users/project/").getAbsolutePath() + "/"; // 파일이 저장될 절대 경로
            String newFileName = "image" + hour + minute + second + millis; // 새로 부여한 이미지명
            String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1"); // 정규식 이용하여 확장자만 추출
            String path = "images/"; // 저장될 폴더 경로

            if (me.getUserID().equals(couple.getMe().getUserID())) {
                diary.setMyDiary(myDiary);
            } else {
                diary.setOtherDiary(myDiary);
            }

            try {
                if(!image.isEmpty()) {
                    File files = new File(absolutePath + path);
                    if(!files.exists()){
                        files.mkdirs(); // mkdir()과 다르게 상위 폴더가 없을 때 상위폴더까지 생성
                    }

                    files = new File(absolutePath + path + "/" + newFileName + fileExtension);
                    image.transferTo(files);


                    diary.setImageName((newFileName + fileExtension));
                    diary.setImageUrl(path);
                    diary.setImageOriName(image.getOriginalFilename());


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            diaryRepository.save(diary);

        }
        else {
            if (me.getUserID().equals(couple.getOther().getUserID())) {
                existingDiary.setOtherDiary(myDiary);
            } else {
                existingDiary.setMyDiary(myDiary);
            }

            diaryRepository.save(existingDiary);
        }

        if (diary == null) return new ResponseEntity<>("null exception", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("200 ok", HttpStatus.CREATED);
    }

    // 일기 수정 ===========================================================================
    @PatchMapping("/diary/edit/{diary_id}")  // file, diaryDate, myDiary, otherDiary, userID
    public ResponseEntity<Object> patchDiary(@PathVariable Integer diary_id, @ModelAttribute DiaryEditDTO diaryEditDTO,
                                             @RequestParam(value = "file", required = false) MultipartFile file) throws ExecutionException, InterruptedException, IOException {
        Diary diary = diaryRepository.findDiaryByDiaryID(diary_id);
        Integer couple_id = diary.getCouple().getCoupleID();


        User me = coupleRepository.findByCoupleID(couple_id).getMe();
        String myNickname = me.getNickname();
        String otherNickname = me.getOtherID();
        Couple couple = coupleRepository.findByCoupleID(couple_id);
        Integer myUserID = couple.getMe().getUserID();
        Integer otherUserID = couple.getOther().getUserID();

        try {

            if(!file.isEmpty()) {

                MultipartFile image = file;

                LocalDateTime now = LocalDateTime.now();
                int hour = now.getHour();
                int minute = now.getMinute();
                int second = now.getSecond();
                int millis = now.get(ChronoField.MILLI_OF_SECOND);

                String absolutePath = new File("/Users/project/").getAbsolutePath() + "/"; // 파일이 저장될 절대 경로
                String newFileName = "image" + hour + minute + second + millis; // 새로 부여한 이미지명
                String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1"); // 정규식 이용하여 확장자만 추출
                String path = "images/"; // 저장될 폴더 경로

                File files = new File(absolutePath + path);
                if(!files.exists()){
                    files.mkdirs(); // mkdir()과 다르게 상위 폴더가 없을 때 상위폴더까지 생성
                }

                files = new File(absolutePath + path + "/" + newFileName + fileExtension);
                image.transferTo(files);


                diary.setImageName((newFileName + fileExtension));
                diary.setImageUrl(path);
                diary.setImageOriName(image.getOriginalFilename());


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (me.getUserID().equals(couple.getMe().getUserID())) {
            diary.setMyDiary(diaryEditDTO.getMyDiary());
        } else {
            diary.setOtherDiary(diaryEditDTO.getMyDiary());
        }

        diaryRepository.save(diary);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("nickname1", myNickname);
        resultMap.put("nickname2", otherNickname);
        resultMap.put("userID1", myUserID);
        resultMap.put("userID2", otherUserID);
        resultMap.put("diaryDTO", diary);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    // 일기 세부 ===========================================================================
    @GetMapping("/diary/detail/{diary_id}")
    public ResponseEntity<Object> editDiary(@PathVariable Integer diary_id, @RequestParam Integer couple_id) throws ExecutionException, InterruptedException, IOException {
        System.out.println("diaryDetail diary_id:: " + diary_id);
        System.out.println("diaryDetail couple_id:: " + couple_id);

        HashMap<String, Object> map = new HashMap<>();
        Diary diary = diaryRepository.findDiaryByDiaryID(diary_id);

        String imageName = diary.getImageName();  // 사진 이름
        String imageUrl = diary.getImageUrl();  // 경로

        InputStream imageStream = new FileInputStream("/Users/project/" + imageUrl + imageName);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        User me = coupleRepository.findByCoupleID(couple_id).getMe();
        String myNickname = me.getNickname();
        String otherNickname = me.getOtherID();
        Integer coupleID = me.getCouple().getCoupleID();
        Couple couple = coupleRepository.findByCoupleID(coupleID);
        Integer myUserID = couple.getMe().getUserID();
        Integer otherUserID = couple.getOther().getUserID();

        map.put("diaryDetail", diary);
        map.put("image", imageByteArray);
        map.put("nickname1", myNickname);
        map.put("nickname2", otherNickname);
        map.put("userID1", myUserID);
        map.put("userID2", otherUserID);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    // 일기 전체 보기 리스트 =================================================================
    @GetMapping("/diary/list/{couple_id}")
    public ResponseEntity<Object> listAllDiary(@PathVariable Integer couple_id) throws ExecutionException, InterruptedException, IOException {
        System.out.println("<diary> couple_id::" + couple_id);

        List<Diary> diaryList = diaryService.listDiary(couple_id);

        return ResponseEntity.ok().body(diaryList);
    }
}
