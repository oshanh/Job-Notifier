package org.oshanh.jobnotifier.dto;

import java.time.LocalDateTime;

public record FosmisEmailMessage(
        Long noticeId,
        String email,
        String title,
        LocalDateTime publishedAt,
        String link
) {
}