package com.otc.himalaya.repo;

import com.otc.himalaya.entity.TradeHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradeHisRepository extends JpaRepository<TradeHis, Long>, JpaSpecificationExecutor<TradeHis> {

}