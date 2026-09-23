package org.essoktani.bank_account_service.entities;

import org.essoktani.bank_account_service.enums.AccountType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = BankAccount.class, name="p1")
public interface AccountProjection {
    public String getId();
    public AccountType getType();
    public Double getBalance();
}
