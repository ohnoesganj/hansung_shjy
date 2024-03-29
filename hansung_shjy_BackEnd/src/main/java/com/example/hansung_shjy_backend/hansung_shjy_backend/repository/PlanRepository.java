package com.example.hansung_shjy_backend.hansung_shjy_backend.repository;

import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Bank;
import com.example.hansung_shjy_backend.hansung_shjy_backend.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer>, JpaSpecificationExecutor<Plan> {

    // Plan All
    @Query(value = "SELECT * FROM Plan p WHERE p.coupleID = :coupleID", nativeQuery = true)
    List<Plan> findByCouple(@Param("coupleID") Integer coupleID);

    // Plan Modify
    @Query(value = "SELECT * FROM Plan p WHERE p.planID = :planID", nativeQuery = true)
    Plan findAllByPlanID(@Param("planID") Integer planID);

    @Query(value = "SELECT * FROM Plan p WHERE p.planID = :planID", nativeQuery = true)
    List<Plan> findPlansByPlanID(@Param("planID") Integer planID);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM Plan p WHERE p.planID = :planID", nativeQuery = true)
    Integer findByPlanID(@Param("planID") Integer planID);

}
