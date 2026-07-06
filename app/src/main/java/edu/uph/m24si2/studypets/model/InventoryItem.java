package edu.uph.m24si2.studypets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory_item")
public class InventoryItem {

    @PrimaryKey
    public int    id          = 0;
    public String name        = "";
    public String type        = "food";
    public int    effectValue = 0;
    public int    quantity    = 0;
    public int    price       = 0;

    public InventoryItem() {}

    public int    getId()               { return id; }
    public void   setId(int v)          { this.id = v; }
    public String getName()             { return name; }
    public void   setName(String v)     { this.name = v; }
    public String getType()             { return type; }
    public void   setType(String v)     { this.type = v; }
    public int    getEffectValue()      { return effectValue; }
    public void   setEffectValue(int v) { this.effectValue = v; }
    public int    getQuantity()         { return quantity; }
    public void   setQuantity(int v)    { this.quantity = v; }
    public int    getPrice()            { return price; }
    public void   setPrice(int v)       { this.price = v; }
}
