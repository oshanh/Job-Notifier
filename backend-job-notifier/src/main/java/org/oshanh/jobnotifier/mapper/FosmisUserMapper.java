package org.oshanh.jobnotifier.mapper;

import org.oshanh.jobnotifier.dto.FosmisUserDto;
import org.oshanh.jobnotifier.model.FosmisUser;

public class FosmisUserMapper {

    public static FosmisUserDto mapToDto(FosmisUser user) {
        FosmisUserDto dto = new FosmisUserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        return dto;
    }


}
