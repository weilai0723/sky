package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoerList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double tunaver = orderMapper.sumByMap(map);
            tunaver = tunaver == null ? 0.0 : tunaver;//判断非空
            turnoerList.add(tunaver);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoerList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
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
            Integer newUser = userMapper.getByTime(beginTime, endTime);
            newUser = newUser == null ? 0 : newUser;//判断非空
            userList.add(newUser);
            Integer users = userMapper.getByTime(null, endTime);
            users = users == null ? 0 : users;//判断非空
            usersList.add(users);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(userList, ","))
                .totalUserList(StringUtils.join(usersList, ","))
                .build();
    }


    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end的天数数据
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
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
            Integer validOrderCount = orderMapper.getByTimeAndStatus(beginTime, endTime, status);
            validOrderCount = validOrderCount == null ? 0 : validOrderCount;//判断非空
            validOrderCountList.add(validOrderCount);
            Integer orderCount = orderMapper.getByTimeAndStatus(beginTime, endTime, null);
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
        Double orderCompletionRate = (double) validOrderCount / totalOrderCount;


//        OrderReportVO orderReportVO = new OrderReportVO();
//        orderReportVO.setDateList(StringUtils.join(dateList,",");
//        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList,",");
//        orderReportVO.setOrderCountList(StringUtils.join(orderCountList,",");
//        orderReportVO.setTotalOrderCount(totalOrderCount);
//        orderReportVO.setValidOrderCount(validOrderCount);
//        orderReportVO.setOrderCompletionRate(orderCompletionRate);

//        return OrderReportVO
//                .builder()
//                .dateList(StringUtils.join(dateList,",")
//                .orderCompletionRate(validOrderCount/totalOrderCount)
//                .orderCountList(StringUtils.join(orderCountList,",")
//                .totalOrderCount(totalOrderCount)
//                .validOrderCount(validOrderCount)
//                .validOrderCountList(StringUtils.join(validOrderCountList,",")
//                .build();
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .build();
    }

    /**
     * TOP10统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = orderMapper.getSalesTop10(beginTime, endTime);
//        List<String> name = new ArrayList<>();
//        List<String> number = new ArrayList<>();
//        for (GoodsSalesDTO goodsSalesDTO : top10) {
//            name.add(goodsSalesDTO.getName());
//            number.add(String.valueOf(goodsSalesDTO.getNumber()));
//        }
//        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
//        salesTop10ReportVO.setNameList(StringUtils.join(name,","));
//        salesTop10ReportVO.setNumberList(StringUtils.join(number,","));

        String nameList = StringUtils.join(top10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(top10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }


    /**
     * 导出数据
     *
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据表，获取数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //基于模版创建新的excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格sheet页
            XSSFSheet sheet = excel.getSheet("sheet1");

            //填充数据
//            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);
            sheet.getRow(1).getCell(1).setCellValue(dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            //第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);

                //获得某一行
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }



            //通过POI将数据导入到excel
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //通过输出流将excel下载到浏览器

    }


}
