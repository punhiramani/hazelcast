package com.f1soft.hazelcast.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table (name = "BALANCE_CERTIFICATE_REQUEST")
public class BalanceCertificateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, precision = 22)
    private Long id;
    @Column(name = "CURRENCY_ID")
    private Long currencyId;
    @Column(name = "BRANCH_ID")
    private Long branchId;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    @Column(name = "CUSTOMER_LOGIN_ID")
    private Long customerLoginId;
    @Column(name = "BALANCE_REQUESTED_DATE")
    private Date balanceRequestedDate;
    @Column(name = "REQUESTED_DATE")
    private Date requestedDate;
}
