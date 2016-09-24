package com.reonios.mscoprototype;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by reonios on 5/17/16.
 */
public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> itemProductList;
    TextView tvTotal;
    Cart cart;

//  TODO: Do not pass textView instead use Fragments
    public CustomAdapter(Context context, ArrayList<Product> productList, TextView tvTotal) {
        this.context = context;
        this.itemProductList = productList;
        this.tvTotal = tvTotal;
    }
    @Override
    public int getCount() {
        return itemProductList.size();
    }
    @Override
    public Object getItem(int position) {
        return itemProductList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item, null);

            TextView tvPrice = (TextView) convertView.findViewById(R.id.quantity_price);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            TextView tvDescription = (TextView) convertView.findViewById(R.id.description);
            TextView tvBarcode = (TextView) convertView.findViewById(R.id.barcode);
            TextView tvQuantity = (TextView) convertView.findViewById(R.id.quantity);
            ImageView imgRemove = (ImageView) convertView.findViewById(R.id.imgRemove);

//            TextView tvTotal = (TextView) convertView.findViewById(R.id.total);
            cart = new Cart(this, itemProductList, tvTotal);

            Product p = itemProductList.get(position);
            tvPrice.setText(p.getQuantityPrice().toString());
            tvTitle.setText(p.getTitle());
            tvDescription.setText(p.getBody());
            tvBarcode.setText(p.getBarcode());
            tvQuantity.setText(p.getQuantity().toString());

            // click listener for remove button
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cart.removeProduct(itemProductList.remove(position));
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}