package com.magenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;
    private String unavailableReason;
}