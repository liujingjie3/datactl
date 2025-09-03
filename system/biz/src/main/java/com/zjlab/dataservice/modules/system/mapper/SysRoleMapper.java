package com.zjlab.dataservice.modules.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.zjlab.dataservice.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 删除角色与用户关系
     * @Author scott
     * @Date 2019/12/13 16:12
     * @param roleId
     */
    @Delete("delete from sys_user_role where role_id = #{roleId}")
    void deleteRoleUserRelation(@Param("roleId") String roleId);


    /**
     * 删除角色与权限关系
     * @Author scott
     * @param roleId
     * @Date 2019/12/13 16:12
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    void deleteRolePermissionRelation(@Param("roleId") String roleId);

    @Select("select r.* from sys_role r join sys_user_role ur on r.id = ur.role_id where ur.user_id = #{userId}")
    List<SysRole> selectRolesByUserId(@Param("userId") String userId);

}
