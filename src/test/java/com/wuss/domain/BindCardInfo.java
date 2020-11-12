package com.wuss.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BindCardInfo implements Serializable {
    private Long id;
    private String customerId;
    private String bankNo;
    private String bankAccount;
    private String bankAccoName;
    private String cnapsCode;
    private String protocolId;
    private Integer createDate;
    private Long createDatetime;
    private BigDecimal money;
}
