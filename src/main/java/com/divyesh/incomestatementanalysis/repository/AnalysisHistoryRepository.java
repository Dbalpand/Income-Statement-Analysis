package com.divyesh.incomestatementanalysis.repository;

import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisHistoryRepository
        extends JpaRepository<AnalysisHistory, Long> {

}