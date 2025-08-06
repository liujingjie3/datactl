package com.zjlab.dataservice.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * 时间格式转化
     */
    // Date 转化成格式化日期字符串
    public static String dateToFormatStr (Date date){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = secondFormat.format(date);
        return dateStr;
    }
    // Date 转化成格式化日期字符串
    public static String dateToFormatStr (Date date, String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String dateStr = format.format(date);
        return dateStr;
    }
    // 格式化日期字符串转化成 Date
    public static Date formatStrToDate (String dateStr) throws ParseException {
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = secondFormat.parse(dateStr);
        return date;
    }
    // 格式化日期字符串转化成 Date
    public static Date formatStrToDate (String dateStr, String formatStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = format.parse(dateStr);
        return date;
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(String timeStamp){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(Long.parseLong(timeStamp));
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(String timeStamp, String formatStr){
        SimpleDateFormat secondFormat = new SimpleDateFormat(formatStr);
        return secondFormat.format(Long.parseLong(timeStamp));
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(long timeStamp){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(timeStamp);
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatDayStr(long timeStamp){
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd");
        return secondFormat.format(timeStamp);
    }
    // 时间戳转化成格式化日期字符串
    public static String timeStampToFormatStr(long timeStamp, String formatStr){
        SimpleDateFormat secondFormat = new SimpleDateFormat(formatStr);
        return secondFormat.format(timeStamp);
    }

    /**
     * 时间获取
     */
    // 获取当前时间戳（精确到毫秒）
    public static long getCurrentTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }
    // 获取当前的年月日时分秒（格式为 yyyy-MM-dd HH:mm:ss 的字符串）
    public static String getCurrentSecond() {
        SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return secondFormat.format(new Date());
    }
    // 获取当前的年月日（格式为 yyyy-MM-dd 的字符串）
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }
    // 获取当前的日（仅数值）
    public static Integer getCurrentD() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
    // 获取当前的年（格式为 yyyy-MM 的字符串）
    public static String getCurrentMonth(String sep) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        return monthFormat.format(new Date());
    }
    // 获取当前的月（仅数值）
    public static Integer getCurrentM() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }
    // 获取当前的年（格式为 yyyy 的字符串）
    public static String getCurrentYear() {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        return yearFormat.format(new Date());
    }
    // 获取当前的年（仅数值）
    public static Integer getCurrentY() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static boolean isDateFormatValid(String dateString, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false); // 设置为非宽松模式，以确保严格的日期格式检查
        try {
            // 尝试解析日期字符串
            sdf.parse(dateString);
            return true; // 解析成功，说明格式正确
        } catch (ParseException e) {
            // 解析失败，格式不正确
            return false;
        }
    }

    public static LocalDateTime getBeforeDays(int expire){
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 提取当前时间的时间部分
        LocalTime time = now.toLocalTime();
        // 计算当前日期之前expire天数的日期
        LocalDate beforeExpireDate = now.toLocalDate().minusDays(expire);
        // 合并日期和时间
        LocalDateTime beforeExpireDateTime = beforeExpireDate.atTime(time);
        return beforeExpireDateTime;
    }

}
