package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: hexin
 * @Date: 2018/4/23 14:00
 * @Description: 分类service实现类
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     * @param categoryName 分类名字
     * @param parentId 父级id
     * @return
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //表示分类可用
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return  ServerResponse.createBySuccessMessage("添加分类成功");
        }
        return  ServerResponse.createByErrorMessage("添加分类失败");
    }

    /**
     * 更新品类名字
     * @param categoryId id
     * @param categoryName 品类名字
     * @return
     */
    @Override
    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null && StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);

        //根据id选择更新
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return  ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return  ServerResponse.createByErrorMessage("更新品类名字失败");
    }
}
