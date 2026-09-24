package org.essoktani.bank_account_service.mappers;

import org.essoktani.bank_account_service.dto.BankAccountRequestDTO;
import org.essoktani.bank_account_service.dto.BankAccountResponseDTO;
import org.essoktani.bank_account_service.entities.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public BankAccount fromRequestDTO(BankAccountRequestDTO dto) {

        return BankAccount.builder()
                .balance(dto.getBalance())
                .currency(dto.getCurrency())
                .type(dto.getType())
                .build();
    }

    public BankAccountResponseDTO fromBankAccount(BankAccount bankAccount) {

        return BankAccountResponseDTO.builder()
                .id(bankAccount.getId())
                .createdAt(bankAccount.getCreatedAt())
                .balance(bankAccount.getBalance())
                .currency(bankAccount.getCurrency())
                .type(bankAccount.getType())
                .build();
    }
}