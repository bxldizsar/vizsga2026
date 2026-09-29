/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.heavymachinery1;

/**
 *
 * @author boldi
 */
//interface: minden amit ellehet adni. gep es alkatresz
// Az Inventory egy ArrayList<Sellable> listan szamolja a teljes keszletertekeket,
// es nem kell tudnia, hogy gep vagy alkatresz van-e a listaban.
public interface Sellable {

    // mennyit er, ha eladjuk (EUR)
    double getSellingValue();

    // mennyibe kerult nekunk (EUR)
    double getPurchaseValue();
}