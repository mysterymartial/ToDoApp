package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.repository.ToDoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ToDoRepository toDoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ToDo todo = toDoRepository.findByUserName(username);
        if (todo == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                todo.getUserName(),
                todo.getPassword(),
                !todo.isLocked(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }
}

