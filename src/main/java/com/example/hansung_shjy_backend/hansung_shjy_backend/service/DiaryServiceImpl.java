package com.example.hansung_shjy_backend.hansung_shjy_backend.service;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.DiaryDTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Diary;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.User;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.DiaryRepository;
import com.example.hansung_shjy_backend.hansung_shjy_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    UserRepository userRepository;



    // 홈 화면 & 전체 보기 리스트 =============================================================
    @Override
    public List<Diary> listDiary(Integer user_id) throws ExecutionException, InterruptedException {
        System.out.println("listDiary:: " + user_id);
        return diaryRepository.findAllByDiaryID(user_id);
    }



    // 일기 저장 ============================================================
    @Override
    public DiaryDTO createDiary(DiaryDTO diaryDTO) throws ExecutionException, InterruptedException {
        Diary diary = new Diary();
        diary.setDiaryDate(diaryDTO.getDiaryDate());
        diary.setMyDiary(diaryDTO.getMyDiary());
        diary.setOtherDiary(diaryDTO.getOtherDiary());
        diary.setUserID(userRepository.findUserByUserID(diaryDTO.getUserID()));
        diaryRepository.save(Diary.toEntity(diaryDTO));
        return diaryDTO;
    }

    // 일기 수정 ============================================================
    @Override
    public DiaryDTO modifyDiary(DiaryDTO diaryDTO) throws ExecutionException, InterruptedException {
        Diary diary = diaryRepository.findByDiaryID(diaryDTO.getUserID(), diaryDTO.getDiaryID());
        System.out.println("modifyDiary:: " + diary);
        if (diary == null) return null;

        diary.setDiaryDate(diaryDTO.getDiaryDate());
        diary.setMyDiary(diary.getMyDiary());
        diary.setOtherDiary(diary.getOtherDiary());
        diary.setUserID(diary.getUserID());
        diaryRepository.save(Diary.toEntity(diaryDTO));
        return diaryDTO;
    }




}
