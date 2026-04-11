package com.socialcommerce.admin;

public interface AdminService {
    void disableUser(String uuid);
    void grantSeller(String uuid);
}
