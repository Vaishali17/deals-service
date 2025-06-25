package com.eatclub.deals.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeakTimeResponse {

    private String peakTimeStart;
    private String peakTimeEnd;

    public PeakTimeResponse(LocalTime peakTimeStart, LocalTime peakTimeEnd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.peakTimeStart = (peakTimeStart != null) ? peakTimeStart.format(formatter) : null;
        this.peakTimeEnd = (peakTimeEnd != null) ? peakTimeEnd.format(formatter) : null;
    }
    
}
