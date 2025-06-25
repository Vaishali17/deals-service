package com.eatclub.deals.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.eatclub.deals.entity.Deal;
import com.eatclub.deals.repository.DealRepository;

@Service
public class PeakTimeCalculatorService {

    private final DealRepository dealRepository;
    private static final int INTERVAL_GRANULARITY_MINUTES = 30;
    private static final int DAY_MINUTES = 24 * 60;
    private static final int NUMBER_OF_SLOTS = DAY_MINUTES / INTERVAL_GRANULARITY_MINUTES;

    public PeakTimeCalculatorService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    public PeakTimeWindow calculatePeakTimeWindow() {
        List<Deal> allDeals = dealRepository.findAllValidDeals();

        if (allDeals == null || allDeals.isEmpty()) {
            return new PeakTimeWindow(null, null);
        }

        List<Integer> slotDealCounts = new ArrayList<>(NUMBER_OF_SLOTS);
        for (int i = 0; i < NUMBER_OF_SLOTS; i++) {
            slotDealCounts.add(0);
        }

        populateDealCountsInSlots(allDeals, slotDealCounts);

        int maximumActiveDeals = findMaxDealCount(slotDealCounts);

        if (maximumActiveDeals <= 0) {
            return new PeakTimeWindow(null, null);
        }

        PeakWindowIndices peakIndices = findLongestPeakWindowIndices(slotDealCounts, maximumActiveDeals);

        LocalTime peakStartTime = LocalTime.MIDNIGHT.plusMinutes(peakIndices.startSlotIndex * INTERVAL_GRANULARITY_MINUTES);
        LocalTime peakEndTime = LocalTime.MIDNIGHT.plusMinutes((peakIndices.startSlotIndex + peakIndices.length) * INTERVAL_GRANULARITY_MINUTES);

        if (peakEndTime.equals(LocalTime.MIDNIGHT) && (peakIndices.startSlotIndex + peakIndices.length) * INTERVAL_GRANULARITY_MINUTES > 0) {
            peakEndTime = LocalTime.MIDNIGHT;
        }

        return new PeakTimeWindow(peakStartTime, peakEndTime);
    }

    private void populateDealCountsInSlots(List<Deal> deals, List<Integer> slotCounts) {
        for (Deal deal : deals) {
            int dealStartMinutes = deal.getStartTime().toSecondOfDay() / 60;
            int dealEndMinutes = deal.getEndTime().toSecondOfDay() / 60;

            int startSlot = dealStartMinutes / INTERVAL_GRANULARITY_MINUTES;
            int endSlot = (dealEndMinutes == 0 && deal.getEndTime().getHour() == 0)
                          ? NUMBER_OF_SLOTS - 1
                          : (dealEndMinutes - 1) / INTERVAL_GRANULARITY_MINUTES;

            if (dealStartMinutes <= dealEndMinutes) {
                for (int i = startSlot; i <= endSlot; i++) {
                    slotCounts.set(i, slotCounts.get(i) + 1);
                }
            } else {
                for (int i = startSlot; i < NUMBER_OF_SLOTS; i++) {
                    slotCounts.set(i, slotCounts.get(i) + 1);
                }
                for (int i = 0; i <= endSlot; i++) {
                    slotCounts.set(i, slotCounts.get(i) + 1);
                }
            }
        }
    }


    private int findMaxDealCount(List<Integer> slotCounts) {
        int max = 0;
        for (int count : slotCounts) {
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    private PeakWindowIndices findLongestPeakWindowIndices(List<Integer> slotCounts, int targetMaxDeals) {
        int longestRunStart = -1;
        int longestRunLength = 0;
        int currentRunStart = -1;
        int currentRunLength = 0;

        for (int i = 0; i < NUMBER_OF_SLOTS; i++) {
            if (slotCounts.get(i) == targetMaxDeals) {
                if (currentRunLength == 0) {
                    currentRunStart = i;
                }
                currentRunLength++;

                if (currentRunLength > longestRunLength) {
                    longestRunLength = currentRunLength;
                    longestRunStart = currentRunStart;
                }
            } else {
                currentRunLength = 0;
                currentRunStart = -1;
            }
        }
        return new PeakWindowIndices(longestRunStart, longestRunLength);
    }

    private static class PeakWindowIndices {
        int startSlotIndex;
        int length;

        PeakWindowIndices(int startSlotIndex, int length) {
            this.startSlotIndex = startSlotIndex;
            this.length = length;
        }
    }

    public static class PeakTimeWindow {
        private LocalTime peakTimeStart;
        private LocalTime peakTimeEnd;

        public PeakTimeWindow(LocalTime peakTimeStart, LocalTime peakTimeEnd) {
            this.peakTimeStart = peakTimeStart;
            this.peakTimeEnd = peakTimeEnd;
        }

        public LocalTime getPeakTimeStart() {
            return peakTimeStart;
        }

        public LocalTime getPeakTimeEnd() {
            return peakTimeEnd;
        }
    }
}
