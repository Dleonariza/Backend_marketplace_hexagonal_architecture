package com.marketplace.hexagonal.Domain.User.Models;

import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleId;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleModel;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserModel {
    private UserId userId;
    private String email;
    private String userName;
    private String password;
    private String name;
    private String avatarUrl;
    private String phone;
    private List<UserAddress> addresses;
    private UserStatus status;
    private Set<RoleModel> roles;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean enabled;
    private boolean accountNoExpired;
    private boolean accountNoLocked;
    private boolean credentialNoExpired;

    public static UserModel create(String email, String userName, String password,
                                   String name, String avatarUrl, String phone,
                                   List<UserAddress> addresses) {
        return UserModel.builder()
                .userId(UserId.generate())
                .email(email)
                .userName(userName)
                .password(password)
                .name(name)
                .avatarUrl(avatarUrl)
                .phone(phone)
                .addresses(new ArrayList<>(addresses))
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>(Set.of(defaultRole())))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    private static RoleModel defaultRole(){
        RoleModel roleModel = new RoleModel();
        roleModel.setId(RoleId.generate());
        roleModel.setRole(RoleEnum.BUYER);
        roleModel.setPermissions(RoleEnum.getDefaultPermissionsFor(RoleEnum.BUYER));
        return roleModel;
    }

    public void block() {
        if (this.status == UserStatus.BLOCKED)
            throw new IllegalStateException("User is already blocked.");
        this.status = UserStatus.BLOCKED;
        this.accountNoLocked = false;
        this.updatedAt = Instant.now();
    }

    public void unblock() {
        if (this.status != UserStatus.BLOCKED)
            throw new IllegalStateException("User is not blocked.");
        this.status = UserStatus.ACTIVE;
        this.accountNoLocked = true;
        this.updatedAt = Instant.now();
    }

    public String addAddress(UserAddress userAddress){
        if (userAddress == null) throw new IllegalArgumentException("Address cannot be null");
        if (userAddress.getIsDefault()) {
            this.clearDefaultAddress();
        }
        this.addresses.add(UserAddress.builder()
                .id(UserAddressId.generate())
                .street(userAddress.getStreet())
                .city(userAddress.getCity())
                .state(userAddress.getState())
                .postalCode(userAddress.getPostalCode())
                .country(userAddress.getCountry())
                .isDefault(userAddress.getIsDefault())
                .build());

        return "Address added";
    }

    private void clearDefaultAddress(){
        this.addresses.forEach(address->address.setIsDefault(false));
    }

    public static UserModel reconstitute(UserId id,
                                         String email,
                                         String userName,
                                         String passwordHash,
                                         String name,
                                         String avatarUrl,
                                         String phone,
                                         List<UserAddress> addresses,
                                         UserStatus status,
                                         Set<RoleModel> roles,
                                         Instant createdAt,
                                         Instant updatedAt,
                                         boolean enabled,
                                         boolean accountNoExpired,
                                         boolean accountNoLocked,
                                         boolean credentialNoExpired) {
        UserModel user = new UserModel();
        user.userId = id;
        user.email = email;
        user.userName = userName;
        user.password = passwordHash;
        user.name = name;
        user.avatarUrl = avatarUrl;
        user.phone = phone;
        user.addresses = addresses != null
                ? addresses
                : new ArrayList<>();
        user.status = status;
        user.roles = roles != null
                ? roles
                : new HashSet<>();
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.enabled = enabled;
        user.accountNoExpired = accountNoExpired;
        user.accountNoLocked = accountNoLocked;
        user.credentialNoExpired = credentialNoExpired;
        return user;
    }
}
