package com.example.AccountProject.domain;

import com.example.AccountProject.type.AccountStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    //Account 테이블 : pk(id)
    @Id
    @GeneratedValue
    private Long id;

    //컬럼 추가
    @ManyToOne //유저를 N:1로 가짐
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    //enum 값에 0,1,2,3을 저장하는게 아니라 AccountStatus클래스의 실제 스트링 값 저장
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    //모든 테이블에 생성, 수정 값 자동으로 저장해줌,
    //@EntityListeners 함께 사용
    //AuditingEntityListener 작동 시키기 위해 //JpaAuditingConfiguration클래스에 @EnableJpaAuditing지정
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
