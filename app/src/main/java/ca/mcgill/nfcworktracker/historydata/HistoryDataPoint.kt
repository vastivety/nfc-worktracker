package ca.mcgill.nfcworktracker.historydata

import java.time.Instant

/**
 * data point for a single history entry.
 */
class HistoryDataPoint(val startTime: Instant, val endTime: Instant) {

    override fun toString(): String {
        return "recorded from $startTime to $endTime."
    }
}
