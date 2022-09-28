package com.waben.option.common.web.socket;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.waben.option.common.configuration.properties.WebConfigProperties;
import com.waben.option.common.web.socket.codec.ConnectHandler;
import com.waben.option.common.web.socket.codec.ExceptionHandle;
import com.waben.option.common.web.socket.codec.HttpRequestHandler;
import com.waben.option.common.web.socket.codec.TextWebSocketFrameHandler;
import com.waben.option.common.web.socket.codec.WebSocketOutHandler;
import com.waben.option.common.web.socket.listener.ConnectListener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServer {

    private final String host;

    private final int port;

    @Resource
    private WebConfigProperties webConfigProperties;

    @Resource
    private TextWebSocketFrameHandler textWebSocketFrameHandler;

    @Resource
    private WebSocketOutHandler webSocketOutHandler;

    @Resource
    private HttpRequestHandler httpRequestHandler;

    @Resource
    private ExceptionHandle exceptionHandler;

    @Resource
    protected ConnectHandler connectHandler;

    private Channel channel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public WebSocketServer(String ip, int port) {
        this.host = ip;
        this.port = port;
    }

    public void start() throws InterruptedException {
        log.info("begin to start rpc server");
        startServer();
    }

    protected void initChannelPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("idleHandler", new IdleStateHandler(webConfigProperties.getWebsocket().getReadIdleTime(), 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(connectHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(10240));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(webSocketOutHandler);
        pipeline.addLast("httpRequestHandle", httpRequestHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(textWebSocketFrameHandler);
        pipeline.addLast("exceptionHandler", exceptionHandler);
    }

    private void startServer() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        WebConfigProperties.WebsocketConfig websocketConfig = webConfigProperties.getWebsocket();
        workerGroup = new NioEventLoopGroup(websocketConfig.getIoThreadNum());
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, websocketConfig.getBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(websocketConfig.getLowMark(), websocketConfig.getHighMark()))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        initChannelPipeline(pipeline);
                    }
                });
        channel = serverBootstrap.bind(host,port).sync().channel();
        log.info("NettyRPC server listening on port " + port + " and ready for connections...");
    }
    
    public void registerListener(ConnectListener listener) {
    	connectHandler.registerListener(listener);
    }

    @PreDestroy
    public void stop() {
        log.info("destroy server resources");
        if (channel == null) {
            log.error("server channel is null");
        } else {
        	channel.closeFuture().syncUninterruptibly();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
