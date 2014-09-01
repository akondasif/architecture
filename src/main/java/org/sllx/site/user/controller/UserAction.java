package org.sllx.site.user.controller;

import org.sllx.site.core.controller.BaseController;
import org.sllx.site.core.support.Page;
import org.sllx.site.user.entity.User;
import org.sllx.site.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserAction extends BaseController {

    private static final String REDIRECT = "redirect:/user";

    @Resource(name = "userService")
    private UserService userService;

    @RequestMapping
    public String list(ServletRequest request, Page page, ModelMap model){
        List<User> users = userService.list(makeParam(request), page);
        model.addAttribute("users", users);
        return "user";
    }

    @RequestMapping("/get")
    public String get(User user, ModelMap model){
        user = userService.get(user);
        model.addAttribute("user", user);
        return "user";
    }


    @RequestMapping(value = "/add")
    public String add(User user){
        userService.insert(user);
        return REDIRECT;
    }

    @RequestMapping("/edit")
    public String edit(User user){
        User dbo = userService.get(user);
        userService.copyValidProp(dbo, user);
        userService.update(dbo);
        return REDIRECT;
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable int id){
        userService.delete(new User(id));
        return REDIRECT;
    }

    @RequestMapping("/json")
    public @ResponseBody
    User json(User user){
        return userService.get(user);
    }

    @RequestMapping("/upload")
    public String upload(@RequestParam("uploadFile") MultipartFile file){
        return REDIRECT;
    }
}