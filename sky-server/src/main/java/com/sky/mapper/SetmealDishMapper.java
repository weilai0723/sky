package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectById(Long id);

    void update(Setmeal setmealDTO);

    void deleteById(List<Long> ids);

    @Delete("delete from setmeal_dish where setmeal_id  = #{id}")
    void delete(Long id);

//   void insert(Setmeal setmeal);
}
