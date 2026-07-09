package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.InventoryItem;

@Dao
public interface InventoryItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(InventoryItem item);

    // Ambil semua item milik user tertentu (untuk shop — semua item tampil)
    @Query("SELECT * FROM inventory_item WHERE username = :username ORDER BY type ASC")
    List<InventoryItem> getSemuaItem(String username);

    // Ambil item yang sudah dibeli (quantity > 0) untuk inventory user
    @Query("SELECT * FROM inventory_item WHERE username = :username AND quantity > 0 ORDER BY name ASC")
    List<InventoryItem> getInventoryUser(String username);

    @Query("UPDATE inventory_item SET quantity = quantity + :jumlah WHERE username = :username AND id = :idItem")
    void tambahQuantity(String username, int idItem, int jumlah);

    @Query("UPDATE inventory_item SET quantity = quantity - 1 WHERE username = :username AND id = :idItem AND quantity > 0")
    void kurangiQuantity(String username, int idItem);

    @Query("SELECT * FROM inventory_item WHERE username = :username AND id = :idItem LIMIT 1")
    InventoryItem getItemById(String username, int idItem);

    @Query("UPDATE inventory_item SET quantity = quantity + :jumlah WHERE username = :username AND name = :namaItem")
    void tambahQuantityByNama(String username, String namaItem, int jumlah);

    @Query("SELECT COUNT(*) FROM inventory_item WHERE username = :username AND quantity > 0")
    int hitungItemDibeli(String username);

    // Hitung total item untuk satu user (untuk cek apakah perlu seed)
    @Query("SELECT COUNT(*) FROM inventory_item WHERE username = :username")
    int hitungItemUser(String username);
}
