package com.example.fullstackbookreviewer.service.user;

import com.example.fullstackbookreviewer.entity.User;

public interface IUserService {
    User getOrCreateUser(String name, String email);
}
