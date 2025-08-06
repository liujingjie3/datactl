package com.zjlab.dataservice.common.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

public class PageUtil {

    @FunctionalInterface
    public interface PageTask<T>{
        /**
         * 分页实现方法，返回具体的数据
         * @return
         */
        public List<T> queryPage();
    }

    /**
     * 通过PageHelper实现分页查询
     * @param pageIndex
     * @param pageSize
     * @param pageTask
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> getPage(Integer pageIndex, Integer pageSize, PageTask<T> pageTask){
        int realPageIndex = pageIndex == null ? 1 : pageIndex;
        int realPageSize = pageSize == null ? 10 : pageSize;

        PageHelper.startPage(realPageIndex, realPageSize);

        List<T> list = pageTask.queryPage();

        PageInfo<T> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }

    public static void pageSplit(Integer pageIndex, Integer pageSize){
        int index = pageIndex == null ? 1 : pageIndex;
        int size = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(index, size);
    }

    public static <T> List<T> getPageData(List<T> list, Integer pageIndex, Integer pageSize){
        List<T> result = new LinkedList<>();
        if (!CollectionUtils.isEmpty(list)){
            int realPageIndex = pageIndex == null ? 1 : pageIndex;
            int realPageSize = pageSize == null ? 10 : pageSize;
            int start = (realPageIndex - 1) * realPageSize;
            if (start > list.size() - 1){
                return result;
            }
            int end = Math.min(start + realPageSize - 1, list.size() - 1);
            for (int i = start; i <= end; ++i){
                result.add(list.get(i));
            }
        }
        return result;
    }

}
