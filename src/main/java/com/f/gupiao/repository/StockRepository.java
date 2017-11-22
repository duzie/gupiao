package com.f.gupiao.repository;

import com.f.gupiao.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * Created by fei on 2017/4/6.
 */
public interface StockRepository extends CrudRepository<Stock, Long> {

    Page<Stock> findAllByOrderByCodeAsc(Pageable pageable);

    Page<Stock> findBySDate(String sDate, Pageable pageable);

    Page<Stock> findByCodeOrderBySDateDesc(String code, Pageable pageable);

    Stock findByName(String name);

    @Transactional
    @Modifying
    Long removeByCodeAndSDate(String code, String sDate);

    @Transactional
    @Modifying
    @Query(value = "update Stock set fall=:fall where id=:id")
    public int updateFall(@Param("fall") int fall, @Param("id") long id);

    @Transactional
    @Modifying
    @Query(value = "delete from Stock where rowid not in (select max(rowid) from Stock where code=:code group by code,s_date) and code=:code", nativeQuery = true)
    void deleteRepeat(@Param("code") String code, @Param("code") String code1);

    @Transactional
    @Modifying
    @Query(value = "delete from Stock where sysdate - 40 > PARSEDATETIME(s_date,'yyyyMMdd')", nativeQuery = true)
    public int deleteOld();


}
