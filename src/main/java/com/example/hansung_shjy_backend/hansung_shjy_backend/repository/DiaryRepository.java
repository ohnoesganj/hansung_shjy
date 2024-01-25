package com.example.hansung_shjy_backend.hansung_shjy_backend.repository;

import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer>, JpaSpecificationExecutor<Diary> {

    // 일기 디테일 화면
    @Query(value = "SELECT * FROM Diary d WHERE d.coupleID = :coupleID AND d.diaryID = :diary_id", nativeQuery = true)
        Diary findByDiaryID(@Param("coupleID") Integer coupleID, @Param("diary_id") Integer diary_id);


    @Query(value = "SELECT * FROM Diary d WHERE d.couple.coupleID = :coupleID", nativeQuery = true)
    Diary findDiaryByCouple(@Param("coupleID") Integer coupleID);

    @Query(value = "SELECT * FROM Diary d WHERE d.coupleid = :coupleID AND d.diary_date = :diaryDate", nativeQuery = true)
    Diary findDiaryByCoupleAndAndDiaryDate(@Param("coupleID") Integer coupleID, @Param("diaryDate") Date diaryDate);


}
