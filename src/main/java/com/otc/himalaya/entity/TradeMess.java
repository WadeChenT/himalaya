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
@Table(name = "trade_mess")
public class TradeMess implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", insertable = false, nullable = false)
    private String id;

    @Column(name = "trade_id", nullable = false)
    private String tradeId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "mess")
    private String mess;

    @Column(name = "pic")
    private Byte pic;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;
}
