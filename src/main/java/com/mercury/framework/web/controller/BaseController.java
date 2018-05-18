package com.mercury.framework.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mercury.common.utils.StringUtils;
import com.mercury.common.utils.security.ShiroUtils;
import com.mercury.framework.web.page.PageDomain;
import com.mercury.framework.web.page.TableDataInfo;
import com.mercury.framework.web.page.TableSupport;
import com.mercury.project.system.user.domain.User;

import java.util.List;

/**
 * web层通用数据处理
 * 
 * @author Sendy
 */
public class BaseController
{
    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = pageDomain.getOrderBy();
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    public User getUser()
    {
        return ShiroUtils.getUser();
    }

    public void setUser(User user)
    {
        ShiroUtils.setUser(user);
    }

    public Long getUserId()
    {
        return getUser().getUserId();
    }

    public String getLoginName()
    {
        return getUser().getLoginName();
    }
}
