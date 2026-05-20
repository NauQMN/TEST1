package com.e0bmanager.server_api.repositories;

import com.e0bmanager.server_api.models.BanAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BanAnRepository extends JpaRepository<BanAn, Integer> {
    // Hàm này bây giờ sẽ hoạt động vì đã có field khuVucId
    List<BanAn> findByKhuVucId(Integer khuVucId);

    // Nếu biến trong Entity là trangthai (viết thường), hàm phải là:
    List<BanAn> findByTrangthai(Integer trangthai);
}
