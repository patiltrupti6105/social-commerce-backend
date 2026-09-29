package com.socialcommerce.admin.service;

public interface AdminService {
    void disableUser(String uuid);
    void enableUser(String uuid);
    void grantSeller(String uuid);
}
