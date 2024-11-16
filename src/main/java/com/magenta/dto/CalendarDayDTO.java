package com.magenta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayDTO {
    private LocalDate date;
    private List<TimeSlotDTO> timeSlots;
    private boolean isAvailable;

    @JsonProperty("isToday")
    private boolean isToday;

    @JsonProperty("isPast")
    private boolean isPast;


}