package com.otc.himalaya.repo;

import com.otc.himalaya.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradeRepository extends JpaRepository<Trade, Long>, JpaSpecificationExecutor<Trade> {

}