package com.example.AccountProject.repository;

import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findFirstByOrderByIdDesc();

    //Account안에 accountUser를 연관관계로 가지고 있기 때문에 가능
    //1인당 가지고 있는 계좌 정보 count
    Integer countByAccountUser(AccountUser accountUser);
}
