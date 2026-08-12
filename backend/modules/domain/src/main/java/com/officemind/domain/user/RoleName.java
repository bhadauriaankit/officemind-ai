package com.officemind.domain.user;

public enum RoleName {
    ADMIN,
    HR,
    FINANCE,
    IT,
    DEVELOPER,
    EMPLOYEE;

    public static RoleName defaultRole() {
        return EMPLOYEE;
    }
}
