package com.yamalc.ytmp.thermoapi.mapper

import com.yamalc.ytmp.thermoapi.domain.UserBodyTemperatures
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.util.*

/**
 * when 0 HIT
 * - domain -> null
 * - List<domain> -> Empty List
 */
@Mapper
interface UserBodyTemperaturesMapper {
    @Select("SELECT * " +
            "FROM user_body_temperatures " +
            "WHERE user_id = #{userId} " +
            "ORDER BY data_timestamp DESC " +
            "LIMIT 1")
    fun selectLatestBodyTemperature(userId: String): UserBodyTemperatures

    @Select("SELECT * " +
            "FROM user_body_temperatures " +
            "WHERE user_id = #{userId} " +
            "AND data_timestamp >= #{fromDate}")
    fun selectBodyTemperaturePeriod(@Param("userId") userId: String, @Param("fromDate") fromDate: Date): Array<UserBodyTemperatures>
}