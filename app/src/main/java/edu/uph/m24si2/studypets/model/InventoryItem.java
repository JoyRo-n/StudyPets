package edu.uph.m24si2.studypets.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Composite primary key: (username, id) — setiap user punya quantity sendiri
// tapi berbagi katalog item yang sama
@Entity(tableName = "inventory_item", primaryKeys = {"username", "id"})
public class InventoryItem {

    @NonNull
    public String username    = ""; // pemilik item
    public int    id          = 0;
    public String name        = "";
    public String type        = "food";
    public int    effectValue = 0;
    public int    quantity    = 0;
    public int    price       = 0;

    public InventoryItem() {}

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
