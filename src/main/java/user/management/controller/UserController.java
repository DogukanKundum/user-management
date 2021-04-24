package user.management.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import user.management.BaseController;
import user.management.config.CustomException;
import user.management.config.JwtTokenProvider;
import user.management.dtos.AuthToken;
import user.management.dtos.LoginUser;
import user.management.dtos.LogoutUser;
import user.management.dtos.NewPassword;
import user.management.dtos.UserDto;
import user.management.impl.intf.UserService;
import user.management.model.User;
import user.management.repository.UserRepository;
import user.management.response.GenericResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;// our nice repository implementation

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public UserController(ModelMapper modelMapper, Gson gson) {
        super(modelMapper, gson);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {
        String token;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            token = jwtTokenProvider.createToken(loginUser.getUsername(), userRepository.findByUsername(loginUser.getUsername()).get().getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        User user = userService.findByUsername(loginUser.getUsername());
        if (user != null) {
            UserDto userDto = new UserDto().getUserFromDto(user);
            userDto.setToken(token);
            userService.save(userDto);
            return ResponseEntity.ok(new AuthToken(token, user.getRoles(), user.getId()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //TODO: ForgetPassword yapılacak.


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public User saveUser(@RequestBody UserDto user) {
        return userService.save(user);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response, @RequestBody LogoutUser token) {
        User user = userService.getUserByToken(token.getToken());
        if (user != null) {
            UserDto userDto = new UserDto().getUserFromDto(user);
            userDto.setToken(null);
            userService.save(userDto);
            HttpSession session;
            SecurityContextHolder.clearContext();
            session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    cookie.setMaxAge(0);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/getUser", method = RequestMethod.GET)
    public ResponseEntity<GenericResponse> getUsers() {
        GenericResponse response = new GenericResponse();
        // returning all persisted users
        response.setData(userService.findAll());
        return success(response);
    }


    @PostMapping(value = "/findByUsername/{userName}")
    public ResponseEntity<GenericResponse> getUserByUsername(@PathVariable("userName") String userName) {
        GenericResponse response = new GenericResponse();
        User user = userService.findByUsername(userName);
        if (user == null) {
            log.info("User does not exist : {}", userName);
            response.setData(user);
            return fail(response);
        }
        response.setData(user);
        return success(response);
    }

    @PostMapping(value = "/save-user-personal")
    public ResponseEntity<GenericResponse> saveUserPersonalInfo(@RequestBody User userDto) {
        GenericResponse response = new GenericResponse();
        User user = userService.findByUsername(userDto.getUsername());
        if (user == null) {
            log.info("User does not exist : {}", userDto.getUsername());
            response.setData(user);
            return fail(response);
        }
        user.setAddress(userDto.getAddress());
        user.setPhone(userDto.getPhone());

        userService.save(convertToUserDto(user));
        response.setData(user);
        return success(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/findByUsername")
    public ResponseEntity<GenericResponse> getUserByUsername(@RequestBody UserDto userPostForm) {
        GenericResponse response = new GenericResponse();
        User user = userService.findByUsername(userPostForm.getUsername());
        if (user == null) {
            log.info("User does not exist : {}", userPostForm.getUsername());
            response.setData(userPostForm.getUsername());
            return notFound(response);
        }
        response.setData(user);
        return success(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<GenericResponse> deleteUser(@RequestBody UserDto userPostForm) {
        GenericResponse response = new GenericResponse();
        User user = userService.findByUsername(userPostForm.getUsername());
        if (user == null) {
            log.info("User does not exist {}", userPostForm.getUsername());
            response.setData(userPostForm.getUsername());
            return notFound(response);
        }
        userService.delete(user);
        return success(response);
    }

    @PostMapping(value = "/updatePassword/{userName}")
    public ResponseEntity<GenericResponse> updatePassword(@RequestBody NewPassword newPassword, @PathVariable("userName") String userName) {
        User user = userService.findByUsername(userName);
        user.setPassword(bcryptEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);
        return success(convertToUserDto(user));
    }

    public UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setAddress(user.getAddress());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setFirstname(user.getFirstname());
        userDto.setPhone(user.getPhone());
        user.setSurname(user.getSurname());
        return userDto;
    }
}