package com.ssrs.platform.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssrs.framework.web.swaggermodel.PageForm;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页查询
 *
 * @author ssrs
 */
public class Query<T> {

    public IPage<T> getPage(Map<String, Object> params) {
        return this.getPage(params, null, false);
    }

    public IPage<T> getPage(PageForm pageForm) {
        Map<String, Object> params = new HashMap<>();
        params.put(PageConstant.PAGENO, pageForm.getPageNo());
        params.put(PageConstant.PAGESIZE, pageForm.getPageSize());
        if (StrUtil.isNotEmpty(pageForm.getSidx())) {
            params.put(PageConstant.ORDER_FIELD, pageForm.getSidx());
        }
        if (StrUtil.isNotEmpty(pageForm.getOrder())) {
            params.put(PageConstant.ORDER, pageForm.getOrder());
        }
        return this.getPage(params, null, false);
    }


    public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        //分页参数
        long pageNo = PageConstant.DEFAULT_PAGENO;
        long pageSize = PageConstant.DEFAULT_PAGESIZE;

        if (params.get(PageConstant.PAGENO) != null) {
            pageNo = Long.parseLong((String) params.get(PageConstant.PAGENO));
        }
        if (params.get(PageConstant.PAGESIZE) != null) {
            pageSize = Long.parseLong((String) params.get(PageConstant.PAGESIZE));
            pageSize = pageSize > PageConstant.MAX_PAGESIZE ? PageConstant.MAX_PAGESIZE : pageSize;
        }

        //分页对象
        Page<T> page = new Page<>(pageNo, pageSize);

        //分页参数
        params.put(PageConstant.PAGENO, page);

        //排序字段
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SqlFilterUtils.sqlInject((String) params.get(PageConstant.ORDER_FIELD));
        String order = (String) params.get(PageConstant.ORDER);


        //前端字段排序
        if (StrUtil.isNotEmpty(orderField) && StrUtil.isNotEmpty(order)) {
            if (PageConstant.ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        //没有排序字段，则不排序
        if (StrUtil.isBlank(defaultOrderField)) {
            return page;
        }

        //默认字段排序
        if (isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        } else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }
}

