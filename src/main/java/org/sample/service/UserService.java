package org.sample.service;

import org.sample.domain.User;

public interface UserService {
    User create(User user);

    User findByUid(String uid);
}
