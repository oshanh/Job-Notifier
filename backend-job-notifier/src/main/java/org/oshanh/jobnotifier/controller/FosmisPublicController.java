package org.oshanh.jobnotifier.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.FosmisUserDto;
import org.oshanh.jobnotifier.service.FosmisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fosmis-notification")
@RequiredArgsConstructor
public class FosmisPublicController {

    private final FosmisService fosmisService;

    @PostMapping
    public ResponseEntity<FosmisUserDto> subscribe(@Valid @RequestBody FosmisUserDto dto) {
        dto.setEnabled(true);
        return new ResponseEntity<>(fosmisService.createUser(dto), HttpStatus.CREATED);
    }
}
