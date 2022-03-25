package kup.get.service.security;

import kup.get.entity.postgres.security.Role;
import kup.get.entity.postgres.security.User;
import kup.get.repository.postgres.security.RoleRepository;
import kup.get.repository.postgres.security.UserRepository;
import kup.get.service.LogService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, ReactiveUserDetailsService {
    @PersistenceContext
    private EntityManager em;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LogService logService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = bCryptPasswordEncoder();

    public UserService(UserRepository userRepository, RoleRepository roleRepository, LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logService = logService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Пользователь не найден");
        return user;
    }

    public User getUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public List<Role> allRoles() {
        return roleRepository.findAll();
    }

    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else {
            User u = userRepository.findFirstById(user.getId());
            if (u == null) {
                return null;
            } else if (!u.getPassword().equals(user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
        }

//        logService.addLog("Добавление пользователя " + user.getUsername() + " с табельным " + user.getTabNum());
        return userRepository.save(user);
    }

    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.deleteById(userId);
//            logService.addLog("Удаление пользователя " + user.get().getUsername());
            return true;
        }
        return false;
    }

    public boolean deleteUser(User user) {
//        logService.addLog("Удаление пользователя " + user.getUsername());
        userRepository.delete(user);
        return true;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return Mono.just(getUserByUsername(s));
    }
}
