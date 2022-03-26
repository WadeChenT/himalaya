package com.otc.himalaya.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "trade")
public class Trade implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", insertable = false, nullable = false)
    private String id;

    @Column(name = "trade_type", nullable = false)
    private Integer tradeType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "b_user_id")
    private String bUserId;

    @Column(name = "b_currency_sign")
    private String bCurrencySign;

    @Column(name = "b_currency")
    private Integer bCurrency;

    @Column(name = "s_user_id")
    private String sUserId;

    @Column(name = "s_currency_sign")
    private String sCurrencySign;

    @Column(name = "s_currency")
    private Integer sCurrency;

    @Column(name = "remark")
    private String remark;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "modifier", nullable = false)
    private String modifier;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;
}
