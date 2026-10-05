package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Auth.Models.PasswordResetToken;
import com.marketplace.hexagonal.Domain.Auth.Models.PendingRegistration;
import com.marketplace.hexagonal.Domain.Auth.Models.RefreshToken;
import com.marketplace.hexagonal.Infrastructura.Entities.Auth.PasswordResetTokenEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Auth.PendingRegistrationEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Auth.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    RefreshToken toModel(RefreshTokenEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken model);

    PendingRegistration toModel(PendingRegistrationEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PendingRegistrationEntity toEntity(PendingRegistration model);

    PasswordResetToken toModel(PasswordResetTokenEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PasswordResetTokenEntity toEntity(PasswordResetToken model);
}
