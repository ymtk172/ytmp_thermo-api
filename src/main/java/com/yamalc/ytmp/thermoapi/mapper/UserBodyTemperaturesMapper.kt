package com.yamalc.ytmp.thermoapi.mapper

import com.yamalc.ytmp.thermoapi.domain.UserBodyTemperatures
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.util.*

@Mapper
interface UserBodyTemperaturesMapper {
    @Select("SELECT user_id, body_temperatures, data_timestamp " +
            "FROM user_body_temperatures " +
            "WHERE user_id = #{userId}")
    fun select(userId: String): List<UserBodyTemperatures>

    @Select("SELECT * " +
            "FROM user_body_temperatures " +
            "WHERE user_id = #{userId} " +
            "ORDER BY data_timestamp DESC " +
            "LIMIT 1")
    fun selectLatestBodyTemperature(userId: String): UserBodyTemperatures

    @Select("SELECT * " +
            "FROM user_body_temperatures " +
            "WHERE user_id = #{userId} " +
            "AND data_timestamp >= #{baseDate}")
    fun selectBodyTemperaturePeriod(@Param("userId") userId: String, @Param("baseDate") baseDate: Date): Array<UserBodyTemperatures>
}