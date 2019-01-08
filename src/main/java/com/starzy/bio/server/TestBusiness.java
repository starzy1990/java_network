package com.starzy.bio.server;

public class TestBusiness {
    public int getPrice(String good){
        return good.equals("yifu")?10:20;
    }
}
