package com.one.frontend.repository;

import com.one.frontend.model.CvsStoreInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CvsStoreInfoRepository {

    @Insert("""
            INSERT INTO cvs_store_info 
            (uuid, cvs_store_id, cvs_store_name, cvs_address, cvs_telephone, cvs_outside)
            VALUES 
            (#{uuid}, #{cvsStoreID}, #{cvsStoreName}, #{cvsAddress}, #{cvsTelephone}, #{cvsOutside})
            """)
    CvsStoreInfo save(CvsStoreInfo storeInfo);

    @Select("SELECT * FROM cvs_store_info WHERE uuid = #{uuid}")
    CvsStoreInfo findByUuid(String uuid);
}
