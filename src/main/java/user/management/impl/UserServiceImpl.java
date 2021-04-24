package user.management.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.management.dtos.UserDao;
import user.management.dtos.UserDto;
import user.management.impl.intf.RoleService;
import user.management.impl.intf.UserService;
import user.management.model.Role;
import user.management.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        log.info("authorities {}", authorities);

        return org.springframework.security.core.userdetails.User//
                .withUsername(username)//
                .password(user.getPassword())//
                .authorities(authorities)//
                .accountExpired(false)//
                .accountLocked(false)//
                .credentialsExpired(false)//
                .disabled(false)//
                .build();
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userDao.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public User findOne(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User save(UserDto user) {
        User userNew = findByUsername(user.getUsername());
        if (userNew == null) {
            userNew = new User();
            userNew.setUsername(user.getUsername());
            userNew.setEmail(user.getEmail());
            userNew.setFirstname(user.getFirstname());
            userNew.setSurname(user.getSurname());
            userNew.setPhone(user.getPhone());
            userNew.setAddress(user.getAddress());
            userNew.setPassword(bcryptEncoder.encode(user.getPassword()));
            Role role = roleService.findByName("USER");
            List<Role> roleSet = new ArrayList<>();
            roleSet.add(role);
            userNew.setRoles(roleSet);
        }
        userNew.setToken(user.getToken());
        return userDao.save(userNew);
    }

    @Override
    public User getUserByToken(String token) {
        return userDao.findByToken(token);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }
}
