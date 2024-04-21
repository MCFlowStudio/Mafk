package com.flow.mafk.database;


import com.flow.mafk.util.object.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CachedDataService {

    private static List<User> userList = new ArrayList<>();

    public static List<User> getUserList() {
        return userList;
    }

    public static Optional<User> findUser(UUID uuid) {
        return userList.stream().filter(data -> data.getUuid().equals(uuid)).findFirst();
    }
}
