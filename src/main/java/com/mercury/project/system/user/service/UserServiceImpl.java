package com.mercury.project.system.user.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mercury.common.constant.UserConstants;
import com.mercury.common.utils.StringUtils;
import com.mercury.common.utils.security.ShiroUtils;
import com.mercury.framework.shiro.service.PasswordService;
import com.mercury.project.system.post.domain.Post;
import com.mercury.project.system.post.mapper.PostMapper;
import com.mercury.project.system.role.domain.Role;
import com.mercury.project.system.role.mapper.RoleMapper;
import com.mercury.project.system.user.domain.User;
import com.mercury.project.system.user.domain.UserPost;
import com.mercury.project.system.user.domain.UserRole;
import com.mercury.project.system.user.mapper.UserMapper;
import com.mercury.project.system.user.mapper.UserPostMapper;
import com.mercury.project.system.user.mapper.UserRoleMapper;

/**
 * 用户 业务层处理
 * 
 * @author Sendy
 */
@Service("userService")
public class UserServiceImpl implements IUserService
{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserPostMapper userPostMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordService passwordService;

    /**
     * 根据条件分页查询用户对象
     * 
     * @param user 用户信息
     * 
     * @return 用户信息集合信息
     */
    @Override
    public List<User> selectUserList(User user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public User selectUserByName(String userName)
    {
        return userMapper.selectUserByName(userName);
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public User selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int batchDeleteUser(Long[] ids)
    {
        userRoleMapper.deleteUserRole(ids);
        userPostMapper.deleteUserPost(ids);
        return userMapper.batchDeleteUser(ids);
    }

    /**
     * 保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int saveUser(User user)
    {
        int count = 0;
        Long userId = user.getUserId();
        if (StringUtils.isNotNull(userId))
        {
            user.setUpdateBy(ShiroUtils.getLoginName());
            // 修改用户信息
            count = userMapper.updateUser(user);
            // 删除用户与角色关联
            userRoleMapper.deleteUserRoleByUserId(userId);
            // 新增用户与角色管理
            insertUserRole(user);
            // 删除用户与岗位关联
            userPostMapper.deleteUserPostByUserId(userId);
            // 新增用户与岗位管理
            insertUserPost(user);

        }
        else
        {
            user.randomSalt();
            user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
            user.setCreateBy(ShiroUtils.getLoginName());
            // 新增用户信息
            count = userMapper.insertUser(user);
            // 新增用户岗位关联
            insertUserPost(user);
            // 新增用户与角色管理
            insertUserRole(user);
        }
        return count;
    }

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetUserPwd(User user)
    {
        user.randomSalt();
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        return userMapper.updateUser(user);
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(User user)
    {
        // 新增用户与角色管理
        List<UserRole> list = new ArrayList<UserRole>();
        for (Long roleId : user.getRoleIds())
        {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            list.add(ur);
        }
        if (list.size() > 0)
        {
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(User user)
    {
        // 新增用户与岗位管理
        List<UserPost> list = new ArrayList<UserPost>();
        for (Long postId : user.getPostIds())
        {
            UserPost up = new UserPost();
            up.setUserId(user.getUserId());
            up.setPostId(postId);
            list.add(up);
        }
        if (list.size() > 0)
        {
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名
     * @return
     */
    @Override
    public String checkUserNameUnique(String loginName)
    {
        int count = userMapper.checkUserNameUnique(loginName);
        if (count > 0)
        {
            return UserConstants.NAME_NOT_UNIQUE;
        }
        return UserConstants.NAME_UNIQUE;
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(Long userId)
    {
        List<Role> list = roleMapper.selectRolesByUserId(userId);
        StringBuffer idsStr = new StringBuffer();
        for (Role role : list)
        {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(Long userId)
    {
        List<Post> list = postMapper.selectPostsByUserId(userId);
        StringBuffer idsStr = new StringBuffer();
        for (Post post : list)
        {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }
}
