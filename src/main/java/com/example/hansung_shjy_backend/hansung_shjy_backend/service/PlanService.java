package com.example.hansung_shjy_backend.hansung_shjy_backend.service;

import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.BankDTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.dto.PlanDTO;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Bank;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Plan;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PlanService {

    // 우리의 여행 계획 첫 화면 =========================================================
    List<Plan> listPlan(Integer user_id) throws ExecutionException, InterruptedException;

    // 우리의 여행 계획 등록 ============================================================
    PlanDTO createPlan(PlanDTO planDTO) throws ExecutionException, InterruptedException;

    // 우리의 여행 계획 수정 ============================================================
    PlanDTO modifyPlan(PlanDTO planDTO) throws ExecutionException, InterruptedException;

    // 우리의 여행 계획 삭제 ============================================================
    String deletePlan(Integer plan_id) throws ExecutionException, InterruptedException;

}
