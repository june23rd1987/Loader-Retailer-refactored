package com.inducesmile.retailer;

/**
 * Created by ravi on 16/11/17.
 */

public class SearchFilterContact {
    private String ProdBrand;
    private String ProdName;
    private String ProdCode;
    private String Amount;
    private String Description;
    private String Image;

    public SearchFilterContact() {
    }

    String getProdBrand() {
        if(ProdBrand == null)
            ProdBrand = "";
        return ProdBrand;
    }

    String getProdName() {
        if(ProdName == null)
            ProdName = "";
        return ProdName;
    }

    String getProdCode() {
        if(ProdCode == null)
            ProdCode = "";
        return ProdCode;
    }

    String getProdAmount() {
        if(Amount == null)
            Amount = "";
        return Amount;
    }

     String getProdInfo() {
        if(Description == null)
            Description = "";
        return Description;
    }

     String getProdImage() {
        if(Image == null)
            Image = "";
        return Image;
    }
}
