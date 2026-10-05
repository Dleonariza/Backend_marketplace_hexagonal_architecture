package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

public enum PermissionEnum {

    READ_PRODUCTS,
    WRITE_PRODUCTS,
    DELETE_PRODUCTS,

    READ_ORDERS,
    WRITE_ORDERS,
    DELETE_ORDERS,
    CANCEL_ORDERS,

    READ_USERS,
    MANAGE_USERS,

    READ_TICKETS,
    WRITE_TICKETS;
}
