package edu.uph.m24si2.studypets.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uph.m24si2.studypets.model.InventoryItem;

@Dao
public interface InventoryItemDao {

    // Simpan item baru (untuk seeding data awal shop)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void simpan(InventoryItem item);

    // Ambil SEMUA item (untuk tampilan shop — termasuk yang belum dibeli)
    @Query("SELECT * FROM inventory_item ORDER BY type ASC")
    List<InventoryItem> getSemuaItem();

    // Ambil item yang sudah dibeli (quantity > 0) untuk inventory
    @Query("SELECT * FROM inventory_item WHERE quantity > 0 ORDER BY name ASC")
    List<InventoryItem> getInventoryUser();

    // Tambah quantity setelah dibeli
    @Query("UPDATE inventory_item SET quantity = quantity + :jumlah WHERE id = :idItem")
    void tambahQuantity(int idItem, int jumlah);

    // Kurangi quantity setelah digunakan
    @Query("UPDATE inventory_item SET quantity = quantity - 1 WHERE id = :idItem AND quantity > 0")
    void kurangiQuantity(int idItem);

    // Ambil satu item berdasarkan id
    @Query("SELECT * FROM inventory_item WHERE id = :idItem LIMIT 1")
    InventoryItem getItemById(int idItem);

    // Tambah quantity berdasarkan nama item (untuk daily login reward)
    @Query("UPDATE inventory_item SET quantity = quantity + :jumlah WHERE name = :namaItem")
    void tambahQuantityByNama(String namaItem, int jumlah);

    // Hitung total item yang sudah pernah dibeli (untuk achievement Shopper)
    @Query("SELECT COUNT(*) FROM inventory_item WHERE quantity > 0")
    int hitungItemDibeli();

    // Cek apakah data shop sudah ada (untuk menghindari seed berulang)
    @Query("SELECT COUNT(*) FROM inventory_item")
    int hitungSemuaItem();
}
