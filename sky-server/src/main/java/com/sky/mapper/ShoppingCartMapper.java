package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    /**
     *    插入购物车数据
     */
    @Insert("insert into shopping_cart " +
            "(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values (#{name}, #{image}, #{userId},#{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Delete(("delete from shopping_cart where user_id = #{userId}"))
    void cleanShoppingCart(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteShoppingCartById(ShoppingCart shoppingCart);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
