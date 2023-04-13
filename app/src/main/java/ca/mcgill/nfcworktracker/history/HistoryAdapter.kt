package ca.mcgill.nfcworktracker.history

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import ca.mcgill.nfcworktracker.NfcService
import ca.mcgill.nfcworktracker.databinding.HistoryEntryBinding
import ca.mcgill.nfcworktracker.databinding.HistoryEntryTimeBinding
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(private val databaseHelper: HistoryDatabaseHelper) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val dataset = ArrayList<HistoryDataPoint>()
    private var recyclerView: RecyclerView? = null

    init {
        reloadFromDatabase()
    }

    /**
     * view holder for a single history entry.
     */
    class ViewHolder(binding: HistoryEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val startTime: TextView
        val startDate: TextView
        val endTime: TextView
        val endDate: TextView
        val timeConnector: ImageView
        private val endTimeBinding: HistoryEntryTimeBinding
        private val noEndTime: View

        init {
            startTime = binding.startTime.time
            startDate = binding.startTime.date
            endTimeBinding = binding.endTime
            endTime = binding.endTime.time
            endDate = binding.endTime.date
            timeConnector = binding.timeConnector
            noEndTime = binding.noEndTime
        }

        /**
         * if argument is false, the end time will be hidden in favor of the noEndTime view.
         */
        fun setEndTimeExistence(hasEndTime: Boolean) {
            if (hasEndTime) {
                endTimeBinding.root.visibility = View.VISIBLE
                noEndTime.visibility = View.GONE
            } else {
                //just invisible because needed for formatting
                endTimeBinding.root.visibility = View.INVISIBLE
                noEndTime.visibility = View.VISIBLE
            }
        }
    }

    /**
     * repopulates dataset member variable with the current database entries.
     */
    private fun reloadFromDatabase() {
        dataset.clear()
        dataset.addAll(databaseHelper.getAllDescending())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearHistory() {
        databaseHelper.deleteAll()
        dataset.clear()
        //notifyItemRangeRemoved(0, itemCount) //does not work
        notifyDataSetChanged()
    }

    @Suppress("unused") // only ever used for testing
    @SuppressLint("NotifyDataSetChanged") // we have no means of knowing at what index that point was inserted
    fun addPoint(point: HistoryDataPoint) {
        databaseHelper.insertIntoDatabase(point)
        reloadFromDatabase()
        notifyDataSetChanged()
    }

    /**
     * creates the view holders.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * updates the view holders, based on current dataset contents.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(parse(dataset[position].startTime, holder.startTime.context)) {
            holder.startTime.text = first
            holder.startDate.text = second
        }
        if (dataset[position].endTime == Instant.MAX) {
            holder.setEndTimeExistence(false)
        } else {
            holder.setEndTimeExistence(true)
            with(parse(dataset[position].endTime, holder.endTime.context)) {
                holder.endTime.text = first
                holder.endDate.text = second
            }
        }
        holder.timeConnector.imageTintList = holder.startTime.textColors
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /**
     * returns dataset size.
     */
    override fun getItemCount(): Int = dataset.size

    /**
     * @return (local time, local date) as strings
     */
    private fun parse(instant: Instant, context: Context): Pair<String, String> {
        val date = Date.from(instant)
        val dateFormat = DateFormat.getMediumDateFormat(context)
        val timeFormat = DateFormat.getTimeFormat(context)

        return Pair(timeFormat.format(date), dateFormat.format(date))
    }

    @SuppressLint("NotifyDataSetChanged") // all items are replaced
    fun notifyNfcServiceStatusChanged(newStatus: Boolean) {
        if (newStatus) {
            //tracking started
            val startTime = NfcService.startTimeOfInstance
            // safety checks
            if (startTime == null || recyclerView?.layoutManager == null) {
                return
            }
            // keep track of whether the list was scrolled before adding new top element
            val topEntryWasVisible = with(recyclerView!!.layoutManager!!) {
                getChildAt(0)?.let {
                    isViewPartiallyVisible(it, true, true)
                }?:false
            }
            // add point to dataset and inform adapter about change
            dataset.add(0, HistoryDataPoint(startTime, Instant.MAX))
            notifyItemInserted(0)
            // if list was at top in the beginning, we also want to stay scrolled to top
            if (topEntryWasVisible) {
                recyclerView!!.scrollToPosition(0)
            }
        } else {
            //tracking stopped
            reloadFromDatabase()
            notifyDataSetChanged()
        }
    }
}
