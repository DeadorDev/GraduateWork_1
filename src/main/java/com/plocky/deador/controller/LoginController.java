package com.plocky.deador.controller;

import com.plocky.deador.global.GlobalData;
import com.plocky.deador.model.PageUrlPrefix;
import com.plocky.deador.model.Role;
import com.plocky.deador.model.User;
import com.plocky.deador.repository.RoleRepository;
import com.plocky.deador.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/login")
    public String login() {
        GlobalData.cart.clear();
        return "/login";
    }

    @GetMapping("/register")
    public String registerGet() {
        return "/register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute("user") User user, HttpServletRequest request, Model model) throws ServletException {
        // Checking if a user exists in the database
        String emailFromForm = user.getEmail();
        User userFromDB = userRepository.findUserByEmailContains(emailFromForm);
        if (!(userFromDB == null)) {
            if (emailFromForm.equals(userFromDB.getEmail())) {
                //AuthenticationError
                PageUrlPrefix authenticationError = new PageUrlPrefix();
                authenticationError.setAuthenticationError("authenticationError");
                model.addAttribute("authenticationError", authenticationError.getAuthenticationError());
                return "/register";
            }
        }
        //
        String password = user.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        userRepository.save(user);
        request.login(user.getEmail(), password);
        return "redirect:/";
    }
}
