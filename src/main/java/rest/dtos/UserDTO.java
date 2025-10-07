package rest.dtos;

import lombok.Builder;
import lombok.Data;

import rest.entities.User;

@Data
@Builder
public class UserDTO {

    Long id;

    String name;

    String email;

    String mobile;

    Integer age;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .age(user.getAge())
                .build();
    }

}
