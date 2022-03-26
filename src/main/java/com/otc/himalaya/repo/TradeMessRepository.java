package com.otc.himalaya.repo;

import com.otc.himalaya.entity.TradeMess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradeMessRepository extends JpaRepository<TradeMess, Long>, JpaSpecificationExecutor<TradeMess> {

}