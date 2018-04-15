package com.mmall.common;

/**
 * @program: mmall
 * @description: 用户const类
 * @author: hexin
 * @create: 2018-04-15 15:49
 **/
public class Const {
    public static  final  String CURRENT_USER = "currentUser";
    public static  final String EMAIL = "email";
    public static  final String USERNAME = "username";
    public  interface  roller{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }
}
