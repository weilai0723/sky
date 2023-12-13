package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal(setmealDTO);
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {

        if (ids != null && ids.size() > 0){
            setmealMapper.deleteById(ids);
            setmealDishMapper.deleteById(ids);
        }else {
            throw new DeletionNotAllowedException(MessageConstant.DELETE_FAILED);
        }
    }

    /**
     * 查询套餐及套餐菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO selectById(Long id) {
        List<SetmealDish> setmealDishes = setmealDishMapper.selectById(id);
        SetmealVO setmealVO= setmealMapper.selectById(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐及套餐菜品
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long id = setmeal.getId();
        setmealDishMapper.delete(id);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
        }
        setmealDishMapper.insert(setmealDishes);

    }

    @Autowired
    private DishMapper dishMapper;
    @Override
    @Transactional
    public void setStatus(Long id, Integer status) {
//        if (status == StatusConstant.DISABLE){
//            setmealMapper.setStatus(id, status);
//        }
//        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDish(id);
//        if (setmealDishes != null && setmealDishes.size() > 0) {
//            for (SetmealDish setmealDish : setmealDishes) {
//                Long dishId = setmealDish.getDishId();
//                Dish dish = dishMapper.getById(dishId);
//                if (dish.getStatus() == StatusConstant.ENABLE) {
//                    //开启
//                    setmealMapper.setStatus(id, status);
//                } else {
//                    //关闭
//                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
//                }
//            }
//        } else {
//            throw new SetmealEnableFailedException("菜品为空");
//        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
        if (status == StatusConstant.ENABLE){
            List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDish(id);
            if (setmealDishes != null && setmealDishes.size() > 0) {
                for (SetmealDish setmealDish : setmealDishes) {
                    Long dishId = setmealDish.getDishId();
                    Dish dish = dishMapper.getById(dishId);
                    if (dish.getStatus() == StatusConstant.DISABLE) {
                        //关闭
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

    }

}
