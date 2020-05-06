package com.yamalc.ytmp.thermoapi

import com.yamalc.ytmp.thermoapi.service.ThermoServiceImpl
import io.grpc.Server
import io.grpc.ServerBuilder
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.IOException
import java.io.Reader
import java.util.logging.Logger


class ThermoApiServer {
    var logger = Logger.getLogger(javaClass.name)
    var server: Server? = null
    var sessionFactory: SqlSessionFactory? = null
    private val MYBATIS_CONFIG = "mybatis-config.xml"

    @Throws(IOException::class)
    fun start() {
        val reader: Reader
        reader = try {
            Resources.getResourceAsReader(MYBATIS_CONFIG)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        val sessionFactory = SqlSessionFactoryBuilder().build(reader)
        server = ServerBuilder
                .forPort(8082)
                .addService(ThermoServiceImpl(sessionFactory))
                .build()
                .start()
        logger.info("start gRPC server.")
    }

    companion object {
        @Throws(IOException::class, InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val userApiServer = ThermoApiServer()
            userApiServer.start()

            //simpleServer.blockUntilShutdown();
            System.console().readLine("> Enter stop.")
            userApiServer.stop()
        }
    }

    fun stop() {
        if (server != null) {
            server!!.shutdown()
            logger.info("shutdown gRPC server.")
        }
    }

    @Throws(InterruptedException::class)
    fun blockUntilShutdown() {
        if (server != null) {
            server!!.awaitTermination()
        }
    }
}