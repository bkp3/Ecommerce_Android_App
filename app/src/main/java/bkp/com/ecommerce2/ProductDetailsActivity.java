package bkp.com.ecommerce2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName, productDescription, productPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addToCartBtn = (FloatingActionButton)findViewById(R.id.add_product_to_cart_btn);
        productImage = (ImageView)findViewById(R.id.product_image_details);
        numberButton = (ElegantNumberButton)findViewById(R.id.number_btn);
        productName = (TextView)findViewById(R.id.product_name_details);
        productDescription = (TextView)findViewById(R.id.product_description_detail);
        productPrice = (TextView)findViewById(R.id.product_price_details);


    }
}
