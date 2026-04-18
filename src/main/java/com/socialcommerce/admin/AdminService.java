package com.socialcommerce.admin;

public interface AdminService {
    void disableUser(String uuid);
    void enableUser(String uuid);
    void grantSeller(String uuid);
}
