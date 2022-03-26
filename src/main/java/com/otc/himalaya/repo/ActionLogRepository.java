package com.otc.himalaya.repo;

import com.otc.himalaya.entity.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, JpaSpecificationExecutor<ActionLog> {

}