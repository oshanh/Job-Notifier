package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO save(UserDTO user){
        User u=new User();
        u.setEmail(user.getEmail());
        u.setName(user.getName());
        u.setPassword(user.getPassword());

        User su=userRepository.save(u);
        UserDTO savedUser= new UserDTO();
        savedUser.setEmail(su.getEmail());
        savedUser.setName(su.getName());
        return savedUser;

    }


    public List<UserDTO> getUsers() {
        List<User> users=userRepository.findAll();
        List<UserDTO> list=new ArrayList<>();
        for(User user:users){
            UserDTO userDTO=new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            list.add(userDTO);
        }
        return list;
    }
}
