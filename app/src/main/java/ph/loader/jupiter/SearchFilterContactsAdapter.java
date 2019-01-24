package com.inducesmile.retailer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jupiter on 12/13/18.
 */

public class SearchFilterContactsAdapter extends RecyclerView.Adapter<SearchFilterContactsAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<SearchFilterContact> productList;
    private List<SearchFilterContact> productListFiltered;
    private ContactsAdapterListener listener;

    public static String PACKAGE_NAME;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, product_description;
        ImageView product_img;

        MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.product_name);
            product_description = view.findViewById(R.id.product_description);
            product_img = view.findViewById(R.id.product_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    SearchFilterContactsAdapter(Context context, List<SearchFilterContact> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = contactList;
        this.productListFiltered = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchfilter_user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final SearchFilterContact contact = productListFiltered.get(position);
        holder.product_name.setText(contact.getProdName());
        //String prodInfoStr = contact.getProdCode() + " "  + contact.getProdAmount() + " "  + contact.getProdInfo();
        String prodInfoStr = "₱" + contact.getProdAmount() + "\n"  + contact.getProdInfo();
        holder.product_description.setText(prodInfoStr);
        holder.product_description.setSingleLine(false);

        PACKAGE_NAME = context.getPackageName();

        Uri otherPath = Uri.parse("android.resource://" + PACKAGE_NAME + "/drawable/" + contact.getProdImage());
        Log.d("otherPath", String.valueOf(otherPath));


        new RequestOptions();
        RequestOptions options = RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Log.d("drawableimg", contact.getProdImage());
        int drawableimg = 999999;
        switch (contact.getProdImage()) {
            case "smart.png":
                drawableimg = R.drawable.smart;
                break;
            case "sun.png":
                drawableimg = R.drawable.sun;
                break;
            case "tnt.png":
                drawableimg = R.drawable.tnt;
                break;
            case "cignal.png":
                drawableimg = R.drawable.cignal;
                break;
            case "pldt.png":
                drawableimg = R.drawable.pldt;
                break;
            case "globe.png":
                drawableimg = R.drawable.globe;
                break;
            case "tm.png":
                drawableimg = R.drawable.tm;
                break;
            case "kuryenteload.png":
                drawableimg = R.drawable.kuryenteload;
                break;
        }

        Glide.with(context)
                .load(drawableimg)
                .apply(options)
                .into(holder.product_img);
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<SearchFilterContact> filteredList = new ArrayList<>();
                    for (SearchFilterContact row : productList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if(!row.getProdBrand().equals("")) {
                            if (row.getProdBrand().toLowerCase().contains(charString.toLowerCase())) {
                                //if (row.getProdAmount().contains(charSequence)) {
                                //if (row.getProdBrand().toLowerCase().contains(charString.toLowerCase()) || row.getProdAmount().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        if(!row.getProdName().equals("")) {
                            if (row.getProdName().toLowerCase().contains(charString.toLowerCase())) {
                                //if (row.getProdAmount().contains(charSequence)) {
                                //if (row.getProdName().toLowerCase().contains(charString.toLowerCase()) || row.getProdAmount().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        if(!row.getProdCode().equals("")) {
                            if (row.getProdCode().toLowerCase().contains(charString.toLowerCase())) {
                                //if (row.getProdAmount().contains(charSequence)) {
                                //if (row.getProdName().toLowerCase().contains(charString.toLowerCase()) || row.getProdAmount().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        if(!row.getProdInfo().equals("")) {
                            if (row.getProdInfo().toLowerCase().contains(charString.toLowerCase())) {
                                //if (row.getProdAmount().contains(charSequence)) {
                                //if (row.getProdName().toLowerCase().contains(charString.toLowerCase()) || row.getProdAmount().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<SearchFilterContact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {

        void onContactSelected(SearchFilterContact contact);

        //void onContactSelected(SearchFilterContact contact);
    }
}
