package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @program: mmall
 * @description: 用户service实现类
 * @author: hexin
 * @create: 2018-04-15 14:54
 **/
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        // todo m5密码加密
        String md5password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //前端不需要密码传入
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
      /*  int resultCount = userMapper.checkUsername(user.getUsername());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        resultCount = userMapper.checkUsername(user.getEmail());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }*/
        if (checkValid(user.getUsername(), Const.USERNAME).isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }

        if (!checkValid(user.getEmail(), Const.EMAIL).isSuccess()) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        //设置为普通用户
        user.setRole(Const.roller.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 参数校验
     *
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {

            if (type.equals(Const.USERNAME)) {
                if (userMapper.checkUsername(str) == 0) {
                    return ServerResponse.createByErrorMessage("用户名不存在");
                }
            }
            if (type.equals(Const.EMAIL)) {
                if (userMapper.checkEmail(str) > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }

        } else {

            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {

        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUserName(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    /**
     * 使用本地缓存检查问题答案的接口
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return
     */
    @Override
    public ServerResponse<String> checkAnser(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+ username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    /**
     *  忘记密码时 重置密码
     * @param username 用户名
     * @param passwordNew 密码
     * @param forgetToken token
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {

        if (StringUtils.isNotBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，forgetToken需要传递");
        }
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isNotBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者已过期");
        }
        //避免null异常
        if (StringUtils.equals(token, forgetToken)) {
            String md5PassowrdNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePassword(username, md5PassowrdNew);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token无效,请重新获取重置密码token");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    /**
     * 在登录时更新面
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param user  user
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     *  根据用户id获取用户信息
     * @param id 用户id
     * @return
     */
    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null){
            ServerResponse.createByErrorMessage("找不到用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
