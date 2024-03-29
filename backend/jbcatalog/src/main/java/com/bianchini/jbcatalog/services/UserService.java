package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.*;
import com.bianchini.jbcatalog.entities.Product;
import com.bianchini.jbcatalog.entities.Role;
import com.bianchini.jbcatalog.entities.User;
import com.bianchini.jbcatalog.repositories.RoleRepository;
import com.bianchini.jbcatalog.repositories.UserRepository;
import com.bianchini.jbcatalog.services.exceptions.DataBaseException;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {  //UserDetailsService é uma interface que contém o método para buscar o usuario no banco de dados

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable){
        Page<User> list = userRepository.findAll(pageable);
        return list.map(x -> new UserDto(x));
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id){
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new UserDto(user);
    }

    @Transactional
    public UserDto saveUser(UserInsertDto userDto) {
        User user = new User();
        copyDtoToEntity(userDto, user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = userRepository.save(user);
        return new UserDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userDto) {
        try {
            User user = userRepository.getOne(id);
            copyDtoToEntity(userDto, user);
            user = userRepository.save(user);
            return new UserDto(user);
        } catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
    }

    public void deleteUser(Long id) {
        try {   
            userRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
        catch (DataIntegrityViolationException e){
            throw new DataBaseException("Integration violation");
        }
    }

    private void copyDtoToEntity(UserDto userDto, User user) {

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user.getRoles().clear();
        for (RoleDto roleDto : userDto.getRoles()) {
            Role role = roleRepository.getOne(roleDto.getId());
            user.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  //Método que busca o usuario.
        User user = userRepository.findByEmail(username);
        if (user == null){
            logger.error("User not found: " + username);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User found: " + username);
        return user;
    }
}
