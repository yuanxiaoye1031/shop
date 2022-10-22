package cn.edu.hit.controller;

import cn.edu.hit.po.User;
import cn.edu.hit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/changeName")
    @ResponseBody
    public String changeName(String name){
        //判断用户名是否可用
        int i =  userService.changeName(name);
        //查找数据库中用户名为name的用户个数，为0才是不重复的用户名
        if(i==0){
            return "ok";
        }
        else {
            return "no";
        }
    }

    @RequestMapping("/toregister")
    public String toregister(){
        return "register";
    }

    @RequestMapping("/doregister")
    public String doregister(User user, Model model){
        //创建对象，准备存入数据库

        //首先去除用户名的多余空格
        if(user.getUsername() == null || user.getUsername().trim().equals("")){
            //获取用户名异常
            model.addAttribute("error","注册失败");
            return "register";  //重新回到注册页面
        }

        //密码正则表达式匹配
        String pwdPattern="^(?=.*[a-zA-Z])(?=.*[\\d])(?=.*[\\W])[a-zA-Z\\d\\W]{6,16}$";

        //检测密码是否符合
        if(!user.getPassword().matches(pwdPattern)){
            model.addAttribute("error","注册失败");
            return "register";
        }
        //电话正则表达式
        String phonePatter="^1[3,5,7,8][\\d]{9}$";
        if(!user.getPhone().matches(phonePatter)){
            model.addAttribute("error","注册失败");
            return "register";
        }

        //添加用户
        userService.addUser(user);

        //注册成功 重定向到登录
        return "redirect:/user/tologin";
    }

    @RequestMapping("/tologin")
    public String tologin(User user){
        return "login";
    }

    @RequestMapping("/dologin")
    public String dologin(User user, String checkBox, HttpSession httpSession, HttpServletResponse response, Model model){
        //一旦登录成功，将用户信息存入httpSession以全局共享


        //检验登录信息    数据库查询到的信息
        User validUser=userService.login(user);
        if(validUser == null){
            //用户错误
            model.addAttribute("error","用户名或密码错误");
            System.out.println("用户名密码错误将重新回到登录页");
            return "redirect:/user/tologin";
        }else {
            //登录正确

            //将用户信息存起来  域对象
            //pagecontext 当前页面有效
            // request  一次请求内邮箱
            // serveletcontext  服务区有效
            // session  一次会话有效

            httpSession.setAttribute("user",validUser);

            System.out.println("\nusername:"+validUser.getUsername());
            System.out.println("pwd:"+validUser.getPassword());

            //是否勾选了记住密码
            if("on".equals(checkBox)) {
                //记住密码
                Cookie nameCookie = new Cookie("name", validUser.getUsername());
                Cookie pwdCookie = new Cookie("pass",validUser.getPassword());

                nameCookie.setPath("/");
                pwdCookie.setPath("/");

                //设置时效
                nameCookie.setMaxAge(60*24*7);
                pwdCookie.setMaxAge(60*24*7);

                //带着Cookie响应
                response.addCookie(nameCookie);
                response.addCookie(pwdCookie);

                System.out.printf("用户【"+user.getUsername()+"】 已登录");
            }else {
                //不记住    或删掉之前存的密码
                Cookie nameCookie = new Cookie("name", validUser.getUsername());
                Cookie pwdCookie = new Cookie("pass",validUser.getPassword());

                nameCookie.setPath("/");
                pwdCookie.setPath("/");

                //设置时效   时效设为0相当于删掉
                nameCookie.setMaxAge(0);
                pwdCookie.setMaxAge(0);

                //带着Cookie响应
                response.addCookie(nameCookie);
                response.addCookie(pwdCookie);

                System.out.printf("用户【"+user.getUsername()+"】 已登录");

            }

            return "redirect:/index/toindex";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
//        Cookie[] cookies=request.getCookies();
        Cookie nameCookie=new Cookie("name","");
        Cookie pwdCookie=new Cookie("pass","");
//
//
//        for(Cookie cookie:cookies){
//            if(cookie.getName().equals("name")){
//                nameCookie=cookie;
//            }
//            if(cookie.getName().equals("pass")){
//                pwdCookie=cookie;
//            }
//        }

        User user=(User)httpSession.getAttribute("user");
        System.out.printf("用户【"+user.getUsername()+"】 已下线");


        //设置时效   时效设为0相当于删掉
        nameCookie.setMaxAge(0);
        pwdCookie.setMaxAge(0);

        nameCookie.setPath("/");
        pwdCookie.setPath("/");

        response.addCookie(nameCookie);
        response.addCookie(pwdCookie);

        httpSession.setAttribute("user","");


        return "index";
    }


}
