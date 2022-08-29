package com.mokasong.user.validation;

import javax.validation.groups.Default;

public class UserDataValidationGroups {
    public interface Register {};
    public interface VerifyPhoneNumber {};
    public interface Login {};

    public interface FindEmail {};

    public interface FindPassword {};
}
