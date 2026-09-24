package org.essoktani.bank_account_service.service;

import org.essoktani.bank_account_service.dto.BankAccountRequestDTO;
import org.essoktani.bank_account_service.dto.BankAccountResponseDTO;

import java.util.List;

public interface AccountService {

    BankAccountResponseDTO addAccount(BankAccountRequestDTO requestDTO);

    List<BankAccountResponseDTO> getAllAccounts();

    BankAccountResponseDTO getAccount(String id);

    BankAccountResponseDTO updateAccount(
            String id,
            BankAccountRequestDTO requestDTO
    );

    void deleteAccount(String id);
}