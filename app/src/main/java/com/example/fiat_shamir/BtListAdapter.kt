package com.example.fiat_shamir

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bt_view.view.*


class BtListAdapter(private val myDataset: List<BluetoothDevice>) :
    RecyclerView.Adapter<BtListAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var view: View = v
        var name = v.deviceName
        var mac = v.deviceMac


        init {
            v.setOnClickListener(this)
        }

        override fun onClick(vi: View?) {
            Log.e(TAG, name.text as String)
            val msg = adapterPosition
            val intent = Intent(vi?.context, Prover::class.java).apply {
                putExtra("device", msg)
            }
            vi?.context?.startActivity(intent)
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val myView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bt_view, parent, false) as View

        return MyViewHolder(myView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.text = myDataset[position].name
        holder.mac.text = myDataset[position].address
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
