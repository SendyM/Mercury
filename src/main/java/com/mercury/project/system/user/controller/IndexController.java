package com.mercury.project.system.user.controller;

import com.mercury.framework.config.MercuryConfig;
import com.mercury.framework.web.controller.BaseController;
import com.mercury.project.system.menu.domain.Menu;
import com.mercury.project.system.menu.service.IMenuService;
import com.mercury.project.system.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页 业务处理
 * 
 * @author Sendy
 */
@Controller
public class IndexController extends BaseController
{
    @Autowired
    private IMenuService menuService;

    @Autowired
    private MercuryConfig mercuryConfig;

    /**
     * 系统首页
     * @param model Model
     * @return String
     */
    @GetMapping("/index")
    public String index(Model model)
    {
        // 取身份信息
        User user = getUser();
        // 根据用户id取出菜单
        List<Menu> menus = menuService.selectMenusByUserId(user.getUserId());
        model.addAttribute("menus", menus);
        model.addAttribute("user", user);
        model.addAttribute("copyrightYear", mercuryConfig.getCopyrightYear());
        return "index";
    }

    /**
     * 系统介绍
     * @param model Model
     * @return String
     */
    @GetMapping("/system/main")
    public String main(Model model)
    {
        model.addAttribute("version", mercuryConfig.getVersion());
        return "main";
    }

}
