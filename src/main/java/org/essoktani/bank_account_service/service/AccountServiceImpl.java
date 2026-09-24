package org.essoktani.bank_account_service.service;

import org.essoktani.bank_account_service.dto.BankAccountRequestDTO;
import org.essoktani.bank_account_service.dto.BankAccountResponseDTO;
import org.essoktani.bank_account_service.entities.BankAccount;
import org.essoktani.bank_account_service.mappers.AccountMapper;
import org.essoktani.bank_account_service.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(
            BankAccountRepository bankAccountRepository,
            AccountMapper accountMapper) {

        this.bankAccountRepository = bankAccountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public BankAccountResponseDTO addAccount(
            BankAccountRequestDTO requestDTO) {

        BankAccount bankAccount =
                accountMapper.fromRequestDTO(requestDTO);

        bankAccount.setId(UUID.randomUUID().toString());

        bankAccount.setCreatedAt(
                new java.util.Date()
        );

        BankAccount savedBankAccount =
                bankAccountRepository.save(bankAccount);

        return accountMapper.fromBankAccount(savedBankAccount);
    }

    @Override
    public List<BankAccountResponseDTO> getAllAccounts() {

        return bankAccountRepository.findAll()
                .stream()
                .map(accountMapper::fromBankAccount)
                .toList();
    }

    @Override
    public BankAccountResponseDTO getAccount(String id) {

        BankAccount bankAccount =
                bankAccountRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        String.format(
                                                "Account %s Not found",
                                                id
                                        )
                                )
                        );

        return accountMapper.fromBankAccount(bankAccount);
    }

    @Override
    public BankAccountResponseDTO updateAccount(
            String id,
            BankAccountRequestDTO requestDTO) {

        BankAccount bankAccount =
                bankAccountRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        String.format(
                                                "Account %s Not found",
                                                id
                                        )
                                )
                        );

        bankAccount.setBalance(requestDTO.getBalance());
        bankAccount.setCurrency(requestDTO.getCurrency());
        bankAccount.setType(requestDTO.getType());

        BankAccount updatedBankAccount =
                bankAccountRepository.save(bankAccount);

        return accountMapper.fromBankAccount(updatedBankAccount);
    }

    @Override
    public void deleteAccount(String id) {

        if (!bankAccountRepository.existsById(id)) {
            throw new RuntimeException(
                    String.format("Account %s Not found", id)
            );
        }

        bankAccountRepository.deleteById(id);
    }
}