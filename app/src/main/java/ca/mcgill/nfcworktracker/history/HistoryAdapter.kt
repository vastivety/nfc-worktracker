package ca.mcgill.nfcworktracker.history

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.mcgill.nfcworktracker.databinding.HistoryEntryBinding
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(private val databaseHelper: HistoryDatabaseHelper) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val dataset = ArrayList<HistoryDataPoint>()

    init {
        reloadFromDatabase()
    }

    /**
     * view holder for a single history entry.
     */
    class ViewHolder(binding: HistoryEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView: TextView
        val startTime: TextView
        val startDate: TextView
        val endTime: TextView
        val endDate: TextView
        val timeConnector: ImageView

        init {
            startTime = binding.startTime.time
            startDate = binding.startTime.date
            endTime = binding.endTime.time
            endDate = binding.endTime.date
            textView = binding.testText
            timeConnector = binding.timeConnector
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
        with(parse(dataset[position].endTime, holder.endTime.context)) {
            holder.endTime.text = first
            holder.endDate.text = second
        }
        holder.timeConnector.imageTintList = holder.startTime.textColors
        holder.textView.text = "HELLO I AM AT POSITION $position. My record says:\n${dataset[position]}"
    }

    /**
     * returns dataset size.
     */
    override fun getItemCount(): Int = dataset.size

    private fun parse(instant: Instant, context: Context): Pair<String, String> {
        val date = Date.from(instant)
        val dateFormat = DateFormat.getMediumDateFormat(context)
        val timeFormat = DateFormat.getTimeFormat(context)

        return Pair(timeFormat.format(date), dateFormat.format(date))
    }
}
