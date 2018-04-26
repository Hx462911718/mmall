package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

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
    public  interface  ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public  interface  roller{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    public  interface  Cart {
        int CHECKED = 1 ; //被选中
        int UN_CHECKED = 0 ;//未被选中
        String LIMT_NUM_FAIL = "LIMT_NUM_FAIL";//限制数量失败
        String LIMT_NUM_SUCCESS = "LIMT_NUM_SUCCESS";
    }
    public enum  ProductStatusEnum{
        ON_SALE("在线",1);
        private  String value;
        private  int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
