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
@Table(name = "user", schema = "otc_tw")
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", insertable = false, nullable = false)
    private String id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_role", nullable = false)
    private Integer role;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "trade_amount")
    private Integer tradeAmount;

    @Column(name = "trade_count")
    private Integer tradeCount;

    @Column(name = "liked_count")
    private Integer likeCount;

    @Column(name = "auth_email")
    private boolean authEmail;

    @Column(name = "auth_discord")
    private boolean authDiscord;

    @Column(name = "tmp_token")
    private String tmpToken;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;
}
