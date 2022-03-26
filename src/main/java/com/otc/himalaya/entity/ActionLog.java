package com.otc.himalaya.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "action_log")
public class ActionLog implements Serializable {

    private static final long serialVersionUID = -5200401135976742094L;

    @Id
//    @SequenceGenerator(name="ACTION_LOG_SEQ",
//            sequenceName="ACTION_LOG_SEQ",
//            allocationSize=1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,
//            generator="ACTION_LOG_SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    @Column(name = "rest_url", nullable = false)
    private String restUrl;

    @Column(name = "req_header")
    private String reqHeader;

    @Column(name = "req_body")
    private String reqBody;

    @Column(name = "res_header")
    private String resHeader;

    @Column(name = "res_body")
    private String resBody;

    @Column(name = "exception_trace")
    private String exceptionTrace;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    
}