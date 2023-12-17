package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单细明数据
     * @param orderDetailsList
     */
    void insetBatch(List<OrderDetail> orderDetailsList);
}
