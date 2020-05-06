package com.yamalc.ytmp.thermoapi.service

import com.yamalc.ytmp.grpc.thermo.BodyTemperatureResponse
import com.yamalc.ytmp.grpc.thermo.ThermoGrpc
import com.yamalc.ytmp.grpc.thermo.UserIdRequest
import com.yamalc.ytmp.thermoapi.domain.UserBodyTemperatures
import com.yamalc.ytmp.thermoapi.mapper.UserBodyTemperaturesMapper
import io.grpc.stub.StreamObserver
import org.apache.ibatis.session.SqlSessionFactory
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.logging.Logger

class ThermoServiceImpl(sqlSessionFactory: SqlSessionFactory) : ThermoGrpc.ThermoImplBase() {
    var logger = Logger.getLogger(javaClass.name)
    companion object private var sessionFactory: SqlSessionFactory = sqlSessionFactory
    private val shortPeriod: Long = 14
    private val longPeriod: Long = 30
    override fun latestBodyTemperature(request: UserIdRequest,
                                       responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: UserBodyTemperatures = try {
            sessionFactory.openSession().use { session ->
                        session.getMapper(UserBodyTemperaturesMapper::class.java).selectLatestBodyTemperature(request.id)
            }
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val response = BodyTemperatureResponse
                .newBuilder()
                .setId(request.id)
                .setBodyTemperature(result.body_temperatures!!)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun latestHealthCheck(request: UserIdRequest,
                                   responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: Array<UserBodyTemperatures> = try {
            sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                       .selectBodyTemperaturePeriod(request.id,
                            Date.from(LocalDate.now()
                                               .minusDays(shortPeriod)
                                               .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val response = BodyTemperatureResponse
                .newBuilder()
                .setId(request.id)
                .setBodyTemperature(result.maxBy { it.body_temperatures!! }?.body_temperatures!!)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun recentlyHealthCheck(request: UserIdRequest,
                                     responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: Array<UserBodyTemperatures> = try {
            sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                        .selectBodyTemperaturePeriod(request.id,
                                Date.from(LocalDate.now()
                                        .minusDays(shortPeriod)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val response = BodyTemperatureResponse
                .newBuilder()
                .setId(request.id)
                .setBodyTemperature(result.map { it.body_temperatures!! }.average().toFloat())
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun normalBodyTemperature(request: UserIdRequest,
                                       responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        val result: Array<UserBodyTemperatures> = try {
            sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                        .selectBodyTemperaturePeriod(request.id,
                                Date.from(LocalDate.now()
                                        .minusDays(longPeriod)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
        } catch (e: IOException) {
            println("DB access error occurred")
            throw e
        }
        val response = BodyTemperatureResponse
                .newBuilder()
                .setId(request.id)
                .setBodyTemperature(result.map { it.body_temperatures!! }.average().toFloat())
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}