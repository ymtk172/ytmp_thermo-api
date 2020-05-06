package com.yamalc.ytmp.thermoapi.service

import com.yamalc.ytmp.grpc.thermo.BodyTemperatureResponse
import com.yamalc.ytmp.grpc.thermo.ResultType
import com.yamalc.ytmp.grpc.thermo.ThermoGrpc
import com.yamalc.ytmp.grpc.thermo.UserIdRequest
import com.yamalc.ytmp.thermoapi.mapper.UserBodyTemperaturesMapper
import io.grpc.stub.StreamObserver
import org.apache.ibatis.session.SqlSessionFactory
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.logging.Logger

class ThermoServiceImpl(sqlSessionFactory: SqlSessionFactory) : ThermoGrpc.ThermoImplBase() {
    var logger: Logger = Logger.getLogger(javaClass.name)
    companion object private var sessionFactory: SqlSessionFactory = sqlSessionFactory
    private val shortDayPeriod: Long = 14
    private val longMonthPeriod: Long = 1
    override fun latestBodyTemperature(request: UserIdRequest,
                                       responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        var resultBodyTemperature = -1F
        val resultType = try {
            val dbAccessResult = sessionFactory.openSession().use { session ->
                        session.getMapper(UserBodyTemperaturesMapper::class.java).selectLatestBodyTemperature(request.id)}
            if (dbAccessResult == null) {
                ResultType.NOT_EXISTS
            } else {
                resultBodyTemperature = dbAccessResult.body_temperatures!!
                // ResultType Declaration must be bottom
                ResultType.SUCCESS
            }
        } catch (e: IOException) {
            logger.warning("DB access error occurred")
            e.printStackTrace()
            ResultType.FAILURE
        }
        val response: BodyTemperatureResponse = BodyTemperatureResponse
                .newBuilder()
                .setResult(resultType)
                .setId(request.id)
                .setBodyTemperature(resultBodyTemperature)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun latestHealthCheck(request: UserIdRequest,
                                   responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        var resultBodyTemperature = -1F
        val resultType = try {
            val dbAccessResult = sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                       .selectBodyTemperaturePeriod(request.id,
                            Date.from(LocalDate.now()
                                               .minusDays(shortDayPeriod)
                                               .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
            if (dbAccessResult.isEmpty()) {
                ResultType.NOT_EXISTS
            } else {
                resultBodyTemperature = dbAccessResult.maxBy { it.body_temperatures!! }?.body_temperatures!!
                // ResultType Declaration must be bottom
                ResultType.SUCCESS
            }
        } catch (e: IOException) {
            logger.warning("DB access error occurred")
            e.printStackTrace()
            ResultType.FAILURE
        }
        val response: BodyTemperatureResponse = BodyTemperatureResponse
                .newBuilder()
                .setResult(resultType)
                .setId(request.id)
                .setBodyTemperature(resultBodyTemperature)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun recentlyHealthCheck(request: UserIdRequest,
                                     responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        var resultBodyTemperature = -1F
        val resultType = try {
            val dbAccessResult = sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                        .selectBodyTemperaturePeriod(request.id,
                                Date.from(LocalDate.now()
                                        .minusDays(shortDayPeriod)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
            if (dbAccessResult.isEmpty()) {
                ResultType.NOT_EXISTS
            } else {
                resultBodyTemperature = dbAccessResult.map { it.body_temperatures!! }.average().toFloat()
                // ResultType Declaration must be bottom
                ResultType.SUCCESS
            }
        } catch (e: IOException) {
            logger.warning("DB access error occurred")
            e.printStackTrace()
            ResultType.FAILURE
        }
        val response: BodyTemperatureResponse = BodyTemperatureResponse
                .newBuilder()
                .setResult(resultType)
                .setId(request.id)
                .setBodyTemperature(resultBodyTemperature)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun normalBodyTemperature(request: UserIdRequest,
                                       responseObserver: StreamObserver<BodyTemperatureResponse>) {
        logger.info(String.format("request: id = %s", request.id))
        var resultBodyTemperature = -1F
        val resultType = try {
            val dbAccessResult = sessionFactory.openSession().use { session ->
                session.getMapper(UserBodyTemperaturesMapper::class.java)
                        .selectBodyTemperaturePeriod(request.id,
                                Date.from(LocalDate.now()
                                        .minusMonths(longMonthPeriod)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))}
            if (dbAccessResult.isEmpty()) {
                ResultType.NOT_EXISTS
            } else {
                resultBodyTemperature = dbAccessResult.map { it.body_temperatures!! }.average().toFloat()
                // ResultType Declaration must be bottom
                ResultType.SUCCESS
            }
        } catch (e: IOException) {
            logger.warning("DB access error occurred")
            e.printStackTrace()
            ResultType.FAILURE
        }
        val response: BodyTemperatureResponse = BodyTemperatureResponse
                .newBuilder()
                .setResult(resultType)
                .setId(request.id)
                .setBodyTemperature(resultBodyTemperature)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}