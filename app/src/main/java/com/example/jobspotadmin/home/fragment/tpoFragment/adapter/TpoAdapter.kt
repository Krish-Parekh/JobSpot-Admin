package com.example.jobspotadmin.home.fragment.tpoFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.databinding.TpoCardLayoutBinding
import com.example.jobspotadmin.home.fragment.tpoFragment.TpoFragment
import com.example.jobspotadmin.model.Tpo

class TpoAdapter(private val listener : TpoFragment) : RecyclerView.Adapter<TpoAdapter.TpoViewHolder>() {

    private val tpoList : MutableList<Tpo> = mutableListOf()

    inner class TpoViewHolder(
        private val binding : TpoCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(tpo : Tpo){
            binding.tvTpoName.text = tpo.username
            binding.tvTpoEmail.text = tpo.email
            binding.ivTpoProfile.load(tpo.imageUri)
            binding.cvPlacementOfficer.setOnClickListener {
                listener.navigateToTpoView(tpo)
            }
            binding.ivDeleteTpo.setOnClickListener {
                listener.deleteTpo(tpo = tpo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TpoViewHolder {
        val view = TpoCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TpoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TpoViewHolder, position: Int) {
        holder.bind(tpoList[position])
    }

    override fun getItemCount(): Int = tpoList.size

    fun setData(newTpo : List<Tpo>){
        tpoList.clear()
        tpoList.addAll(newTpo)
        notifyDataSetChanged()
    }
}