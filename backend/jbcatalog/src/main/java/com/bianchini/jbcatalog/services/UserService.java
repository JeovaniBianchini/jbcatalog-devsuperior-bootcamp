package com.bianchini.jbcatalog.services;

import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.dto.RoleDto;
import com.bianchini.jbcatalog.dto.UserDto;
import com.bianchini.jbcatalog.dto.UserInsertDto;
import com.bianchini.jbcatalog.entities.Product;
import com.bianchini.jbcatalog.entities.Role;
import com.bianchini.jbcatalog.entities.User;
import com.bianchini.jbcatalog.repositories.RoleRepository;
import com.bianchini.jbcatalog.repositories.UserRepository;
import com.bianchini.jbcatalog.services.exceptions.DataBaseException;
import com.bianchini.jbcatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService {

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
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User user = userRepository.getReferenceById(id);
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
            Role role = roleRepository.getReferenceById(roleDto.getId());
            user.getRoles().add(role);
        }
    }
}
