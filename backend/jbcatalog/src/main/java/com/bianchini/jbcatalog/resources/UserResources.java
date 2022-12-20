package com.bianchini.jbcatalog.resources;

import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.dto.UserDto;
import com.bianchini.jbcatalog.dto.UserInsertDto;
import com.bianchini.jbcatalog.dto.UserUpdateDto;
import com.bianchini.jbcatalog.services.ProductService;
import com.bianchini.jbcatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserResources {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAll(Pageable pageable){
        Page<UserDto> list = userService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id){
        UserDto dto = userService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserInsertDto userInsertDto){
        UserDto dto = userService.saveUser(userInsertDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userDto){
        UserDto dto = userService.updateUser(id, userDto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
