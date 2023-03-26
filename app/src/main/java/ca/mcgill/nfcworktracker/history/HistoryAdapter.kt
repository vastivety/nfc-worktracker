package ca.mcgill.nfcworktracker.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.mcgill.nfcworktracker.R

class HistoryAdapter(private val dataset: Array<HistoryDataPoint>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    /**
     * view holder for a single history entry.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.test_text)
        }
    }

    /**
     * creates the view holders.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_entry, parent, false)

        return ViewHolder(view)
    }

    /**
     * updates the view holders, based on current dataset contents.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "HELLO I AM AT POSITION $position. My record says:\n${dataset[position]}"
    }

    /**
     * returns dataset size.
     */
    override fun getItemCount(): Int = dataset.size
}
