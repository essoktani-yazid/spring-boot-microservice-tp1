package org.essoktani.bank_account_service.repositories;

import org.essoktani.bank_account_service.entities.BankAccount;
import org.essoktani.bank_account_service.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BankAccountRepository extends JpaRepository<BankAccount,String>{
    List<BankAccount> findByType(AccountType type);
}
