package com.abranlezama.fullstackbookreviewer.service.user;

import com.abranlezama.fullstackbookreviewer.entity.User;

public interface IUserService {
    User getOrCreateUser(String name, String email);
}
