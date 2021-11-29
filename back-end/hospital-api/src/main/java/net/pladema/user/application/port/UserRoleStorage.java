package net.pladema.user.application.port;

import net.pladema.user.domain.UserRoleBo;

import java.util.List;

public interface UserRoleStorage {

    List<UserRoleBo> getRolesByUser(Integer userId);
}
