//package com.shwan.lakeSidehotel.controller;
//
//
//import com.shwan.lakeSidehotel.exception.UserAlreadyExistsException;
//import com.shwan.lakeSidehotel.model.User;
//import com.shwan.lakeSidehotel.request.LoginRequest;
//import com.shwan.lakeSidehotel.service.IUserService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final IUserService userService;
//
//    @PostMapping("/register-user")
//    public ResponseEntity<?> registerUser(User user) {
//     try {
//         userService.registerUser(user);
//         return ResponseEntity.ok("Registration successful!");
//     } catch (UserAlreadyExistsException e) {
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
//     }
//    }
//
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
//
//    }
//
//}
