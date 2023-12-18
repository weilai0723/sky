package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoerList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double tunaver= orderMapper.sumByMap(map);
            tunaver = tunaver == null ? 0.0 : tunaver;//判断非空
            turnoerList.add(tunaver);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoerList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> userList = new ArrayList<>();
        List<Integer> usersList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

//            Map map = new HashMap();
//            map.put("begin",beginTime);
//            map.put("end",endTime);
            Integer newUser= userMapper.getByTime(beginTime,endTime);
            newUser = newUser == null ? 0: newUser;//判断非空
            userList.add(newUser);
            Integer users= userMapper.getByTime(null,endTime);
            users = users == null ? 0 : users;//判断非空
            usersList.add(users);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(userList,","))
                .totalUserList(StringUtils.join(usersList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> validOrderCountList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

//            Map map = new HashMap();
//            map.put("begin",beginTime);
//            map.put("end",endTime);
            Integer status = Orders.COMPLETED;
            Integer validOrderCount= orderMapper.getByTimeAndStatus(beginTime,endTime,status);
            validOrderCount = validOrderCount == null ? 0: validOrderCount;//判断非空
            validOrderCountList.add(validOrderCount);
            Integer orderCount= orderMapper.getByTimeAndStatus(beginTime,endTime,null);
            orderCount = orderCount == null ? 0 : orderCount;//判断非空
            orderCountList.add(orderCount);
        }
        Integer validOrderCount = 0;
        for (Integer validCount : validOrderCountList) {
            validOrderCount += validCount;
        }
        Integer totalOrderCount = 0;
        for (Integer count : orderCountList) {
            totalOrderCount += count;
        }
        Double orderCompletionRate =0.5;


        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(dateList.toString());
        orderReportVO.setValidOrderCountList(validOrderCountList.toString());
        orderReportVO.setValidOrderCountList(orderCountList.toString());
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(validOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);

        return orderReportVO;
//                OrderReportVO
//                .builder()
//                .dateList(StringUtils.join(dateList,",")
//                .orderCompletionRate(validOrderCount/totalOrderCount)
//                .orderCountList(StringUtils.join(orderCountList,",")
//                .totalOrderCount(totalOrderCount)
//                .validOrderCount(validOrderCount)
//                .validOrderCountList(StringUtils.join(validOrderCountList,",")
//                .build();
    }
}
